package com.popflix.domain.review.exception;

import lombok.Getter;

@Getter
public class ReviewNotFoundException extends ReviewException {
    private final Long reviewId;

    public ReviewNotFoundException(Long reviewId) {
        super(String.format("리뷰를 찾을 수 없습니다. ID: %d", reviewId));
        this.reviewId = reviewId;
    }
}
