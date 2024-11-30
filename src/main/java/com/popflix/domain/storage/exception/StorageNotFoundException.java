package com.popflix.domain.storage.exception;

public class StorageNotFoundException extends RuntimeException {
    public StorageNotFoundException(Long storageId) {
        super("보관함을 찾을 수 없습니다.: " + storageId);
    }
}
