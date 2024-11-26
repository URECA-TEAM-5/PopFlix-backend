package com.popflix.domain.movie.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(Long inputId) {
      super(String.format("영화 정보를 찾을 수 없습니다. Id: %s", inputId));
    }
}