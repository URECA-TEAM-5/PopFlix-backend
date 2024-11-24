package com.popflix.domain.photoreview.exception;

public abstract class PhotoReviewException extends RuntimeException {
    protected PhotoReviewException(String message) {
        super(message);
    }
}
