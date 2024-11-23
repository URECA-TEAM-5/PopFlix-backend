package com.popflix.domain.photoreview.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PhotoReviewCommentResponseDto {
    private Long commentId;
    private String comment;
    private UserInfo user;
    private LocalDateTime createdAt;
    private long likeCount;
    private boolean isLiked;
    private boolean isHidden;
    private List<PhotoReviewReplyResponseDto> replies;

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String nickname;
        private String profileImageUrl;
    }
}

