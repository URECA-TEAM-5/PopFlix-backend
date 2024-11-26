package com.popflix.domain.report.service;

import com.popflix.domain.report.dto.ReportPostDto;
import com.popflix.domain.report.dto.ReportListResponseDto;
import com.popflix.domain.report.dto.ReportResponseDto;
import com.popflix.domain.report.dto.ReportDetailResponseDto;
import com.popflix.domain.report.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {
    ReportResponseDto createReport(ReportPostDto requestDto);
    ReportDetailResponseDto getReport(Long reportId);
    Page<ReportListResponseDto> getReports(ReportStatus status, Pageable pageable);
    void updateReportStatus(Long reportId, ReportStatus status);
}