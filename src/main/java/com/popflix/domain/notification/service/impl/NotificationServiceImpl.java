package com.popflix.domain.notification.service.impl;

import com.popflix.domain.notification.dto.EmailRequestDto;
import com.popflix.domain.notification.dto.NotificationResponseDto;
import com.popflix.domain.notification.entity.Notification;
import com.popflix.domain.notification.enums.NotificationChannel;
import com.popflix.domain.notification.enums.NotificationType;
import com.popflix.domain.notification.exception.NotificationException;
import com.popflix.domain.notification.exception.NotificationNotFoundException;
import com.popflix.domain.notification.repository.NotificationRepository;
import com.popflix.domain.notification.service.EmailService;
import com.popflix.domain.notification.service.EmitterService;
import com.popflix.domain.notification.service.NotificationService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterService emitterService;
    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onCompletion(() -> {
            log.info("SSE Connection Completed for user: {}", userId);
            emitterService.remove(userId);
        });

        emitter.onTimeout(() -> {
            log.info("SSE Connection Timed out for user: {}", userId);
            emitterService.remove(userId);
        });

        emitter.onError((e) -> {
            log.error("SSE Connection Error for user: {}", userId, e);
            emitterService.remove(userId);
        });

        // 연결 즉시 더미 이벤트 전송 (503 에러 방지)
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("Connected successfully"));
        } catch (IOException e) {
            throw new NotificationException("연결 중 에러가 발생했습니다.", e);
        }

        emitterService.save(userId, emitter);
        return emitter;
    }

    @Override
    @Transactional
    public void sendNotification(Long userId, NotificationType type, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

        Notification notification = Notification.builder()
                .type(type)
                .channel(NotificationChannel.SSE)
                .content(content)
                .user(user)
                .build();

        notificationRepository.save(notification);

        if (emitterService.exists(userId)) {
            try {
                emitterService.get(userId).ifPresent(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("notification")
                                .data(NotificationResponseDto.from(notification)));
                        notification.markAsSent();
                    } catch (IOException e) {
                        emitterService.remove(userId);
                        notification.markAsFailed();
                        throw new NotificationException("알림 전송에 실패했습니다.", e);
                    }
                });
            } catch (Exception e) {
                log.error("Failed to send notification to user: {}", userId, e);
                notification.markAsFailed();
            }
        }
    }

    @Override
    @Transactional
    public void sendEmailNotification(Long userId, String subject, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotificationException("사용자를 찾을 수 없습니다."));

        Notification notification = Notification.builder()
                .type(NotificationType.NEW_REVIEW)
                .channel(NotificationChannel.EMAIL)
                .content(content)
                .user(user)
                .build();

        notificationRepository.save(notification);

        try {
            emailService.sendEmail(EmailRequestDto.builder()
                    .toEmail(user.getEmail())
                    .subject(subject)
                    .content(content)
                    .build());

            notification.markAsSent();
        } catch (Exception e) {
            notification.markAsFailed();
            throw new NotificationException("이메일 전송에 실패했습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId).stream()
                .map(NotificationResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new NotificationException("해당 알림에 대한 권한이 없습니다.");
        }

        notification.markAsRead();
    }
}