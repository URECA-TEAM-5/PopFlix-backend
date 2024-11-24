package com.popflix.global.event.listener;

import com.popflix.domain.notification.enums.NotificationType;
import com.popflix.domain.notification.service.NotificationService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.global.event.dto.ReviewCreatedEvent;
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
            List<User> targetUsers = userRepository.findByGenreIds(event.getGenreIds());

            String notificationContent = createNotificationContent(event);

            for (User user : targetUsers) {
                notificationService.sendNotification(
                        user.getUserId(),
                        NotificationType.NEW_REVIEW,
                        notificationContent
                );

                notificationService.sendEmailNotification(
                        user.getUserId(),
                        "새로운 리뷰가 등록되었습니다",
                        notificationContent
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
        return String.format("'%s' 영화에 새로운 리뷰가 등록되었습니다: %s",
                event.getMovieTitle(),
                event.getReviewContent()
        );
    }
}