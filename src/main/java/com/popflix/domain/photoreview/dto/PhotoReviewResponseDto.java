package com.popflix.domain.photoreview.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PhotoReviewResponseDto {
    private Long reviewId;
    private String review;
    private String reviewImage;
    private MovieInfo movie;
    private UserInfo user;
    private LocalDateTime createdAt;
    private long likeCount;
    private long commentCount;
    private boolean isHidden;

    @Getter
    @Builder
    public static class MovieInfo {
        private Long movieId;
        private String title;
        private String posterPath;
    }

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String profileImage;
    }
}