package com.popflix.domain.review.exception;


public abstract class ReviewException extends RuntimeException {
    protected ReviewException(String message) {
        super(message);
    }
}
