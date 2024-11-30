package com.popflix.domain.storage.exception;

public class DuplicateMovieException extends RuntimeException {
    public DuplicateMovieException(String movieName) {
      super("이미 보관함에 등록된 영화입니다: " + movieName);
    }
}
