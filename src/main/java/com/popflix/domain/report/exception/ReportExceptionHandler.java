package com.popflix.domain.report.exception;

import com.popflix.global.util.ApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ReportExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ApiUtil.ApiError<String>> handleReportNotFound(ReportNotFoundException e) {
        log.error("ReportNotFoundException", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiUtil.error(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(DuplicateReportException.class)
    public ResponseEntity<ApiUtil.ApiError<String>> handleDuplicateReport(DuplicateReportException e) {
        log.error("DuplicateReportException", e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiUtil.error(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiUtil.ApiError<String>> handleIllegalState(IllegalStateException e) {
        log.error("IllegalStateException", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiUtil.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}