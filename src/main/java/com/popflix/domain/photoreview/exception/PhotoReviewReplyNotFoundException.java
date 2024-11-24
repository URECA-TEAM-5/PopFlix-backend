package com.popflix.domain.photoreview.exception;

import lombok.Getter;

@Getter
public class PhotoReviewReplyNotFoundException extends PhotoReviewException {
    private final Long replyId;

    public PhotoReviewReplyNotFoundException(Long replyId) {
        super(String.format("대댓글을 찾을 수 없습니다. ID: %d", replyId));
        this.replyId = replyId;
    }
}
