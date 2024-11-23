package com.popflix.domain.photoreview.exception;

import lombok.Getter;

@Getter
public class PhotoReviewLikeNotFoundException extends PhotoReviewException {
    private final Long reviewId;
    private final Long userId;

    public PhotoReviewLikeNotFoundException(Long reviewId, Long userId) {
        super(String.format("포토리뷰 좋아요를 찾을 수 없습니다. ReviewId: %d, UserId: %d", reviewId, userId));
        this.reviewId = reviewId;
        this.userId = userId;
    }
}

