package com.popflix.domain.report.dto;

import com.popflix.domain.report.enums.ReportReason;
import com.popflix.domain.report.enums.ReportStatus;
import com.popflix.domain.report.enums.ReportTarget;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponseDto {
    private Long reportId;
    private ReportTarget targetType;
    private Long targetId;
    private ReportReason reason;
    private ReportStatus status;
    private UserInfo reporter;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}
