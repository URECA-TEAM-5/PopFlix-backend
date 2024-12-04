package com.popflix.auth.exception;

public class InvalidCookieException extends RuntimeException {
    public InvalidCookieException(String message) {
        super(message);
    }
}