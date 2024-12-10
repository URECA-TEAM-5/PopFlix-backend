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
                // 각 사용자별 이벤트 생성
                ReviewCreatedEvent userEvent = ReviewCreatedEvent.builder()
                        .reviewId(event.getReviewId())
                        .movieId(event.getMovieId())
                        .movieTitle(event.getMovieTitle())
                        .reviewContent(event.getReviewContent())
                        .type(event.getType())
                        .reviewer(event.getReviewer())
                        .reviewerNickname(event.getReviewerNickname())
                        .genreIds(event.getGenreIds())
                        .targetUser(user)
                        .build();

                // SSE 알림 전송
                notificationService.sendNotification(userEvent);

                // 이메일 알림 전송
                String content = createNotificationContent(userEvent);
                notificationService.sendEmailNotification(
                        user.getUserId(),
                        "새로운 리뷰가 등록되었습니다.",
                        content
                );
            }

            log.info("Successfully processed review creation event for review ID: {}, total recipients: {}",
                    event.getReviewId(), targetUsers.size());

        } catch (Exception e) {
            log.error("Error processing review creation event for review ID: {}",
                    event.getReviewId(), e);
            throw e;
        }
    }

    private String createNotificationContent(ReviewCreatedEvent event) {
        return String.format("%s님이 '%s' 영화에 새로운 %s를 남겼습니다: %s",
                event.getReviewerNickname(),
                event.getMovieTitle(),
                event.getType() == NotificationType.NEW_REVIEW ? "리뷰" : "포토리뷰",
                event.getReviewContent()
        );
    }
}