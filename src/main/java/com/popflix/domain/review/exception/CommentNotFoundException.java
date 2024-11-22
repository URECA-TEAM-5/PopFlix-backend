package com.popflix.domain.review.exception;

import lombok.Getter;

@Getter
public class CommentNotFoundException extends ReviewException {
    private final Long commentId;

    public CommentNotFoundException(Long commentId) {
        super(String.format("댓글을 찾을 수 없습니다. ID: %d", commentId));
        this.commentId = commentId;
    }
}
