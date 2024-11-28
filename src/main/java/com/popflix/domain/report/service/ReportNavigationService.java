package com.popflix.domain.report.service;

import com.popflix.domain.report.dto.ReportNavigationDto;

public interface ReportNavigationService {
    ReportNavigationDto getNavigationInfo(Long reportId);
}
