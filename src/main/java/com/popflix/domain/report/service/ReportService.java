package com.popflix.domain.report.service;

import com.popflix.domain.report.dto.*;
import com.popflix.domain.report.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    ReportDetailResponseDto createReport(ReportPostDto requestDto);
    ReportDetailResponseDto getReport(Long reportId);
    Page<AdminReportResponseDto> getAdminReports(ReportStatus status, Pageable pageable);
    void updateReportStatus(Long reportId, ReportStatus status);
}