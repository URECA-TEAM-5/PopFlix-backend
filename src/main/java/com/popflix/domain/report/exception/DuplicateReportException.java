package com.popflix.domain.report.exception;

import lombok.Getter;

@Getter
public class DuplicateReportException extends ReportException {
    private final Long reporterId;
    private final Long targetId;

    public DuplicateReportException(Long reporterId, Long targetId) {
        super("이미 신고한 컨텐츠입니다.");
        this.reporterId = reporterId;
        this.targetId = targetId;
    }
}
