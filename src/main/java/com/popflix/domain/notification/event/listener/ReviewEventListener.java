package com.popflix.domain.notification.event.listener;

import com.popflix.domain.notification.enums.NotificationType;
import com.popflix.domain.notification.service.NotificationService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.domain.notification.event.dto.ReviewCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Async
    @EventListener
    @Transactional
    public void handleReviewCreated(ReviewCreatedEvent event) {
        try {
            // 해당 장르를 선택한 유저들 조회
            List<User> targetUsers = userRepository.findByGenreIds(event.getGenreIds());

            for (User user : targetUsers) {
                String content = createNotificationContent(event);

                // SSE 알림 전송
                notificationService.sendNotification(
                        user.getUserId(),
                        event.getType(),
                        content
                );

                // 이메일 알림 전송
                notificationService.sendEmailNotification(
                        user.getUserId(),
                        "새로운 리뷰가 등록되었습니다.",
                        content
                );
            }

            log.info("Successfully processed review creation event for review ID: {}",
                    event.getReviewId());

        } catch (Exception e) {
            log.error("Error processing review creation event", e);
            throw e;
        }
    }

    private String createNotificationContent(ReviewCreatedEvent event) {
        return String.format("%s 님이 '%s' 영화에 새로운 리뷰를 남겼습니다: %s",
                event.getReviewerNickname(),
                event.getMovieTitle(),
                event.getReviewContent()
        );
    }
}