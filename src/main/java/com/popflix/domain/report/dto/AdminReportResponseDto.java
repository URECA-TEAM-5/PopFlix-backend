package com.popflix.domain.report.dto;

import com.popflix.domain.report.enums.ReportStatus;
import com.popflix.domain.report.enums.ReportTarget;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminReportResponseDto {
    private Long reportId;
    private Long reporterId;
    private String reporterNickname;
    private String reporterEmail;
    private Long authorId;
    private String authorNickname;
    private String authorEmail;
    private ReportTarget targetType;
    private Long targetId;
    private String targetContent;
    private ReportStatus status;
    private LocalDateTime reportedAt;
    private String navigationUrl;
}
