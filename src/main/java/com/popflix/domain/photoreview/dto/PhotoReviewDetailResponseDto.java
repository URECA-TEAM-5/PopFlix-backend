package com.popflix.domain.photoreview.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PhotoReviewDetailResponseDto {
    private Long reviewId;
    private String review;
    private String reviewImageUrl;
    private PhotoReviewResponseDto.MovieInfo movie;
    private PhotoReviewResponseDto.UserInfo user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;
    private boolean isLiked;
    private List<PhotoReviewCommentResponseDto> comments;
    private boolean isHidden;
}
