package com.popflix.domain.report.controller;

import com.popflix.domain.report.dto.ReportPostDto;
import com.popflix.domain.report.dto.ReportListResponseDto;
import com.popflix.domain.report.dto.ReportResponseDto;
import com.popflix.domain.report.dto.ReportDetailResponseDto;
import com.popflix.domain.report.enums.ReportStatus;
import com.popflix.domain.report.service.ReportService;
import com.popflix.global.util.ApiUtil;
import com.popflix.global.util.ApiUtil.ApiSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ApiSuccess<?> createReport(
            @Valid @RequestBody ReportPostDto requestDto
    ) {
        ReportResponseDto response = reportService.createReport(requestDto);
        return ApiUtil.success(response);
    }

    @GetMapping("/{reportId}")
    public ApiSuccess<?> getReport(
            @PathVariable Long reportId
    ) {
        ReportDetailResponseDto response = reportService.getReport(reportId);
        return ApiUtil.success(response);
    }

    @GetMapping
    public ApiSuccess<?> getReports(
            @RequestParam(required = false, defaultValue = "PENDING") ReportStatus status,
            Pageable pageable
    ) {
        Page<ReportListResponseDto> response = reportService.getReports(status, pageable);
        return ApiUtil.success(response);
    }

    @PatchMapping("/{reportId}/status")
    public ApiSuccess<?> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status
    ) {
        reportService.updateReportStatus(reportId, status);
        return ApiUtil.success("신고가 처리되었습니다.");
    }
}