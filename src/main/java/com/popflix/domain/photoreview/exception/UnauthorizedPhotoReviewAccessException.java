package com.popflix.domain.photoreview.exception;

public class UnauthorizedPhotoReviewAccessException extends PhotoReviewException {
    public UnauthorizedPhotoReviewAccessException(String message) {
        super(message);
    }
}
