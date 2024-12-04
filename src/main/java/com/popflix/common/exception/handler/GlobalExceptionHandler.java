package com.popflix.common.exception.handler;

import com.popflix.auth.exception.*;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.storage.exception.AccessStorageDeniedException;
import com.popflix.domain.storage.exception.DuplicateMovieException;
import com.popflix.domain.storage.exception.DuplicateStorageNameException;
import com.popflix.domain.storage.exception.StorageNotFoundException;
import com.popflix.domain.user.exception.DuplicateEmailException;
import com.popflix.domain.user.exception.DuplicateNicknameException;
import com.popflix.domain.user.exception.UserNotFoundException;
import com.popflix.global.util.ApiUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error("Handling {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        ApiUtil.ApiError<String> error = ApiUtil.error(statusCode.value(), "서버 오류가 발생했습니다. 관리자에게 문의해주세요.");
        return super.handleExceptionInternal(ex, error, headers, statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("유효성 검사 실패");

        ApiUtil.ApiError<String> error = ApiUtil.error(status.value(), errorMessage);
        return new ResponseEntity<>(error, headers, status);
    }

    @ExceptionHandler(InvalidTokenException.class)
    protected ResponseEntity<?> handleInvalidTokenException(InvalidTokenException e) {
        log.error("Invalid token: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    protected ResponseEntity<?> handleTokenExpiredException(TokenExpiredException e) {
        log.error("Token expired: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication failed: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), "인증에 실패했습니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Access denied: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiUtil.error(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다."));
    }

    @ExceptionHandler(DuplicateStorageNameException.class)
    protected ResponseEntity<?> handleDuplicateStorageNameException(DuplicateStorageNameException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(DuplicateMovieException.class)
    protected ResponseEntity<?> handleDuplicateMovieException(DuplicateMovieException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(AccessStorageDeniedException.class)
    protected ResponseEntity<?> handleAccessStorageDeniedException(AccessStorageDeniedException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiUtil.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @ExceptionHandler(StorageNotFoundException.class)
    protected ResponseEntity<?> handleStorageNotFoundException(StorageNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiUtil.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(MovieNotFoundException.class)
    protected ResponseEntity<?> handleMovieNotFoundException(MovieNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiUtil.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<?> handleUserNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiUtil.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    protected ResponseEntity<?> handleDuplicateEmailException(DuplicateEmailException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    protected ResponseEntity<?> handleDuplicateNicknameException(DuplicateNicknameException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleAllUncaughtException(Exception e) {
        log.error("Uncaught exception occurred: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "서버 오류가 발생했습니다. 관리자에게 문의해주세요."));
    }
}