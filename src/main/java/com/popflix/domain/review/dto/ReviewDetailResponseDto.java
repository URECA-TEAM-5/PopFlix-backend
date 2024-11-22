package com.popflix.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewDetailResponseDto {
    private Long reviewId;
    private String review;
    private ReviewResponseDto.MovieInfo movie;
    private ReviewResponseDto.UserInfo user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;
    private boolean isLiked;
    private List<CommentResponseDto> comments;
    private boolean isHidden;
}
