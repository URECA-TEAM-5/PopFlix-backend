package com.popflix.domain.review.exception;

public class UnauthorizedReviewAccessException extends ReviewException {
    public UnauthorizedReviewAccessException(String message) {
        super(message);
    }
}
