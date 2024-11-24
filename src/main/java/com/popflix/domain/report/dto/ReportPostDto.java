package com.popflix.domain.report.dto;

import com.popflix.domain.report.enums.ReportReason;
import com.popflix.domain.report.enums.ReportTarget;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportPostDto {
    @NotNull(message = "신고 대상 유형은 필수입니다.")
    private ReportTarget targetType;

    @NotNull(message = "신고 대상 ID는 필수입니다.")
    private Long targetId;

    @NotNull(message = "신고 사유는 필수입니다.")
    private ReportReason reason;

    @NotNull(message = "신고자 ID는 필수입니다.")
    private Long reporterId;

    @Builder
    public ReportPostDto(ReportTarget targetType, Long targetId, ReportReason reason, Long reporterId) {
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.reporterId = reporterId;
    }
}
