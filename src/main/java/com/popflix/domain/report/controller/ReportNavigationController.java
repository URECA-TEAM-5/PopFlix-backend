package com.popflix.domain.report.controller;

import com.popflix.domain.report.dto.ReportNavigationDto;
import com.popflix.domain.report.service.ReportNavigationService;
import com.popflix.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportNavigationController {

    private final ReportNavigationService reportNavigationService;

    // 신고된 컨텐츠로 이동하기 위한 정보 조회
    @GetMapping("/{reportId}/navigation")
    public ApiUtil.ApiSuccess<ReportNavigationDto> getNavigationInfo(
            @PathVariable Long reportId
    ) {
        ReportNavigationDto navigationInfo = reportNavigationService.getNavigationInfo(reportId);
        return ApiUtil.success(navigationInfo);
    }
}
