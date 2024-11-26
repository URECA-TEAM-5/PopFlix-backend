package com.popflix.domain.photoreview.exception;

import lombok.Getter;

@Getter
public class PhotoReviewCommentNotFoundException extends PhotoReviewException {
    private final Long commentId;

    public PhotoReviewCommentNotFoundException(Long commentId) {
        super(String.format("댓글을 찾을 수 없습니다. ID: %d", commentId));
        this.commentId = commentId;
    }
}
