package com.popflix.domain.photoreview.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PhotoReviewListResponseDto {
    private Long reviewId;
    private String review;
    private String reviewImage;
    private String movieTitle;
    private LocalDateTime createdAt;
    private long likeCount;
    private long commentCount;
}