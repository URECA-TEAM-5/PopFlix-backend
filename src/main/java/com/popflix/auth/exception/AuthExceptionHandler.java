package com.popflix.auth.exception;

import com.popflix.auth.util.CookieUtil;
import com.popflix.global.util.ApiUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final CookieUtil cookieUtil;

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException e, HttpServletResponse response) {
        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleTokenExpiredException(TokenExpiredException e, HttpServletResponse response) {
        cookieUtil.deleteAccessTokenCookie(response);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<?> handleRefreshTokenException(RefreshTokenException e, HttpServletResponse response) {
        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(InvalidCookieException.class)
    public ResponseEntity<?> handleInvalidCookieException(InvalidCookieException e, HttpServletResponse response) {
        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(BlacklistedTokenException.class)
    public ResponseEntity<?> handleBlacklistedTokenException(BlacklistedTokenException e, HttpServletResponse response) {
        cookieUtil.deleteAccessTokenCookie(response);
        cookieUtil.deleteRefreshTokenCookie(response);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), "인증에 실패했습니다: " + e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiUtil.error(HttpStatus.FORBIDDEN.value(), "접근이 거부되었습니다: " + e.getMessage()));
    }
}