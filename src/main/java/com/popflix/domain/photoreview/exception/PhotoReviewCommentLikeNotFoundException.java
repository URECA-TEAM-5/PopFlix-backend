package com.popflix.domain.photoreview.exception;

import lombok.Getter;

@Getter
public class PhotoReviewCommentLikeNotFoundException extends PhotoReviewException {
    private final Long commentId;
    private final Long userId;

    public PhotoReviewCommentLikeNotFoundException(Long commentId, Long userId) {
        super(String.format("댓글 좋아요를 찾을 수 없습니다. CommentId: %d, UserId: %d", commentId, userId));
        this.commentId = commentId;
        this.userId = userId;
    }
}