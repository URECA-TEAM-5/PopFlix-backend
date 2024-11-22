package com.popflix.domain.review.exception;

import lombok.Getter;

@Getter
public class ReviewLikeNotFoundException extends ReviewException {
    private final Long reviewId;
    private final Long userId;

    public ReviewLikeNotFoundException(Long reviewId, Long userId) {
        super(String.format("리뷰 좋아요를 찾을 수 없습니다. ReviewId: %d, UserId: %d", reviewId, userId));
        this.reviewId = reviewId;
        this.userId = userId;
    }
}
