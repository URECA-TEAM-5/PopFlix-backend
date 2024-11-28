package com.popflix.domain.report.dto;

import com.popflix.domain.report.enums.ReportReason;
import com.popflix.domain.report.enums.ReportStatus;
import com.popflix.domain.report.enums.ReportTarget;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportDetailResponseDto {
    private Long reportId;
    private ReportTarget targetType;
    private Long targetId;
    private ReportReason reason;
    private ReportStatus status;
    private ReportUserInfo reporter;
    private ReportContentInfo content;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class ReportUserInfo {
        private Long userId;
        private String nickname;
        private String email;
    }

    @Getter
    @Builder
    public static class ReportContentInfo {
        private String content;
        private LocalDateTime createdAt;
        private String url;
    }
}