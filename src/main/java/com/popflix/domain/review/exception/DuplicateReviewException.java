package com.popflix.domain.review.exception;

public class DuplicateReviewException extends ReviewException {
    public DuplicateReviewException(String message) {
        super(message);
    }
}
