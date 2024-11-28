package com.popflix.domain.report.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportNavigationDto {
    private Long targetId;
    private Long movieId;
    private String navigationUrl;
    private String content;
    private Long authorId;
    private String authorName;
}