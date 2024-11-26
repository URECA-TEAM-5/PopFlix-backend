package com.popflix.domain.movie.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long inputId) {
        super(String.format("사용자 프로필을 찾을 수 없습니다. Id: %s", inputId));
    }
}
