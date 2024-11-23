package com.popflix.domain.photoreview.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class PhotoReviewExceptionHandler {

    @ExceptionHandler(PhotoReviewNotFoundException.class)
    public ResponseEntity<PhotoReviewErrorResponse> handlePhotoReviewNotFound(
            PhotoReviewNotFoundException e,
            HttpServletRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(PhotoReviewCommentNotFoundException.class)
    public ResponseEntity<PhotoReviewErrorResponse> handlePhotoReviewCommentNotFound(
            PhotoReviewCommentNotFoundException e,
            HttpServletRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(PhotoReviewReplyNotFoundException.class)
    public ResponseEntity<PhotoReviewErrorResponse> handlePhotoReviewReplyNotFound(
            PhotoReviewReplyNotFoundException e,
            HttpServletRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(PhotoReviewLikeNotFoundException.class)
    public ResponseEntity<PhotoReviewErrorResponse> handlePhotoReviewLikeNotFound(
            PhotoReviewLikeNotFoundException e,
            HttpServletRequest request) {
        return createErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedPhotoReviewAccessException.class)
    public ResponseEntity<PhotoReviewErrorResponse> handleUnauthorizedAccess(
            UnauthorizedPhotoReviewAccessException e,
            HttpServletRequest request) {
        return createErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), request);
    }

    @ExceptionHandler(PhotoReviewImageProcessingException.class)
    public ResponseEntity<PhotoReviewErrorResponse> handleImageProcessing(
            PhotoReviewImageProcessingException e,
            HttpServletRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PhotoReviewErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        return createErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PhotoReviewErrorResponse> handleAllUncaughtException(
            Exception e,
            HttpServletRequest request) {
        log.error("Unexpected error occurred", e);
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다.",
                request
        );
    }

    private ResponseEntity<PhotoReviewErrorResponse> createErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {
        PhotoReviewErrorResponse errorResponse = PhotoReviewErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
