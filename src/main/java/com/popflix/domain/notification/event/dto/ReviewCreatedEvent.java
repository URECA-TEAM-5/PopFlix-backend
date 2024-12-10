package com.popflix.domain.notification.event.dto;

import com.popflix.domain.notification.enums.NotificationType;
import com.popflix.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewCreatedEvent {
    private final Long reviewId;
    private final Long movieId;
    private final List<Long> genreIds;
    private final LocalDateTime createdAt;
    private final String reviewContent;
    private final String movieTitle;
    private final NotificationType type;
    private final User reviewer;
    private final String reviewerNickname;
    private final User targetUser;
}