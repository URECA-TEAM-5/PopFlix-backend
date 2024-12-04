package com.popflix.auth.exception;

public class BlacklistedTokenException extends RuntimeException {
    public BlacklistedTokenException(String message) {
        super(message);
    }
}
