package com.popflix.domain.photoreview.exception;

import lombok.Getter;

@Getter
public class PhotoReviewNotFoundException extends PhotoReviewException {
    private final Long reviewId;

    public PhotoReviewNotFoundException(Long reviewId) {
        super(String.format("포토리뷰를 찾을 수 없습니다. ID: %d", reviewId));
        this.reviewId = reviewId;
    }
}
