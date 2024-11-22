package com.popflix.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private Long commentId;
    private String comment;
    private UserInfo user;
    private LocalDateTime createdAt;
    private long likeCount;
    private boolean isLiked;
    private boolean isHidden;

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}
