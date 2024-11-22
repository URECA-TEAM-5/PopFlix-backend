package com.popflix.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewListResponseDto {
    private Long reviewId;
    private String review;
    private String movieTitle;
    private LocalDateTime createdAt;
    private long likeCount;
    private long commentCount;
}
