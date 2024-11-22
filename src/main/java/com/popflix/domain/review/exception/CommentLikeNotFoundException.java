package com.popflix.domain.review.exception;

import lombok.Getter;

@Getter
public class CommentLikeNotFoundException extends ReviewException {
    private final Long commentId;
    private final Long userId;

    public CommentLikeNotFoundException(Long commentId, Long userId) {
        super(String.format("댓글 좋아요를 찾을 수 없습니다. CommentId: %d, UserId: %d", commentId, userId));
        this.commentId = commentId;
        this.userId = userId;
    }
}
