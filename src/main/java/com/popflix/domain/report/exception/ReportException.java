package com.popflix.domain.report.exception;

public abstract class ReportException extends RuntimeException {
    protected ReportException(String message) {
        super(message);
    }
}