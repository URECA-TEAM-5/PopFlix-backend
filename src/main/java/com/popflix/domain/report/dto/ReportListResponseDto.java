package com.popflix.domain.report.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReportListResponseDto {
    private List<ReportResponseDto> reports;
    private long totalCount;
    private long pendingCount;
}