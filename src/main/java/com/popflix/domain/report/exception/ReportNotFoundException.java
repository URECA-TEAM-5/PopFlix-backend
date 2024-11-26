package com.popflix.domain.report.exception;

import lombok.Getter;

@Getter
public class ReportNotFoundException extends ReportException {
    private final Long reportId;

    public ReportNotFoundException(Long reportId) {
        super(String.format("신고를 찾을 수 없습니다. ID: %d", reportId));
        this.reportId = reportId;
    }
}