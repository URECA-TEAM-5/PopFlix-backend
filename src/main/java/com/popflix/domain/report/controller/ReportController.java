package com.popflix.domain.report.controller;

import com.popflix.domain.report.dto.ReportPostDto;
import com.popflix.domain.report.dto.AdminReportResponseDto;
import com.popflix.domain.report.dto.ReportDetailResponseDto;
import com.popflix.domain.report.enums.ReportStatus;
import com.popflix.domain.report.service.ReportNavigationService;
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
    private final ReportNavigationService reportNavigationService;

    // 신고 생성
    @PostMapping
    public ApiSuccess<?> createReport(
            @Valid @RequestBody ReportPostDto requestDto
    ) {
        ReportDetailResponseDto response = reportService.createReport(requestDto);
        return ApiUtil.success(response);
    }

    // 관리자용 신고 목록 조회
    @GetMapping("/admin")
    public ApiSuccess<?> getAdminReports(
            @RequestParam(required = false, defaultValue = "PENDING") ReportStatus status,
            Pageable pageable
    ) {
        Page<AdminReportResponseDto> response = reportService.getAdminReports(status, pageable);
        return ApiUtil.success(response);
    }

    // 신고 상세 정보 조회
    @GetMapping("/{reportId}")
    public ApiSuccess<?> getReport(
            @PathVariable Long reportId
    ) {
        ReportDetailResponseDto response = reportService.getReport(reportId);
        return ApiUtil.success(response);
    }

    // 신고 상태 업데이트 (승인/거절)
    @PatchMapping("/{reportId}/status")
    public ApiSuccess<?> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status
    ) {
        reportService.updateReportStatus(reportId, status);
        return ApiUtil.success("신고가 처리되었습니다.");
    }
}