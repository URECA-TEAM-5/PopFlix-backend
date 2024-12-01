package com.popflix.domain.storage.exception;

public class DuplicateStorageNameException extends RuntimeException {
  public DuplicateStorageNameException(String storageName) {
    super("이미 존재하는 보관함 이름입니다: " + storageName);
  }
}
