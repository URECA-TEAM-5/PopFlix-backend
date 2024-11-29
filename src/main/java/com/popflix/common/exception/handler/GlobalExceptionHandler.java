package com.popflix.common.exception.handler;

import com.popflix.domain.storage.exception.AccessStorageDeniedException;
import com.popflix.domain.storage.exception.DuplicateMovieException;
import com.popflix.domain.storage.exception.DuplicateStorageNameException;
import com.popflix.domain.storage.exception.StorageNotFoundException;
import com.popflix.global.util.ApiUtil;
import com.popflix.global.util.ApiUtil.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        log.error(ex.getMessage(), ex);

        ApiUtil.ApiError<String> error = ApiUtil.error(statusCode.value(), "알 수 없는 오류가 발생했습니다. 문의 바랍니다.");
        return super.handleExceptionInternal(ex, error, headers, statusCode, request);
    }

    @ExceptionHandler(DuplicateStorageNameException.class)
    protected ResponseEntity<?> handleDuplicateStorageNameException(DuplicateStorageNameException e) {
        log.error(e.getMessage(), e);
        ApiError<String> error = ApiUtil.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(error);
    }

    @ExceptionHandler(DuplicateMovieException.class)
    protected ResponseEntity<?> handleDuplicateStorageNameException(DuplicateMovieException e) {
        log.error(e.getMessage(), e);
        ApiError<String> error = ApiUtil.error(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(error);
    }

    @ExceptionHandler(AccessStorageDeniedException.class)
    protected ResponseEntity<?> handleAccessDeniedException(AccessStorageDeniedException e) {
        log.error(e.getMessage(), e);
        ApiUtil.ApiError<String> error = ApiUtil.error(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(error);
    }

    @ExceptionHandler(StorageNotFoundException.class)
    protected ResponseEntity<?> handleAccessDeniedException(StorageNotFoundException e) {
        log.error(e.getMessage(), e);
        ApiUtil.ApiError<String> error = ApiUtil.error(HttpServletResponse.SC_FORBIDDEN, e.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(error);
    }


}
