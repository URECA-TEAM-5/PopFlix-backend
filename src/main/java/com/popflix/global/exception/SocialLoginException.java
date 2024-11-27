package com.popflix.global.exception;

public class SocialLoginException extends RuntimeException {
    public SocialLoginException(String message) {
        super(message);
    }
}