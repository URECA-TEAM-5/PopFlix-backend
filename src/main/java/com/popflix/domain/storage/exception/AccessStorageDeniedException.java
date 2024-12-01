package com.popflix.domain.storage.exception;

public class AccessStorageDeniedException extends RuntimeException {
    public AccessStorageDeniedException(String message) {
        super(message);
    }
}
