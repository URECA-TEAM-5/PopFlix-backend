package com.popflix.domain.user.exception;

public class InvalidGenreException extends RuntimeException {
    public InvalidGenreException() {
        super("유효하지 않은 장르입니다.");
    }
}
