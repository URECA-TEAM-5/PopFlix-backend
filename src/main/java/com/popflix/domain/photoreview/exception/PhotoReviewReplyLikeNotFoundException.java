package com.popflix.domain.photoreview.exception;

import lombok.Getter;

@Getter
public class PhotoReviewReplyLikeNotFoundException extends PhotoReviewException {
    private final Long replyId;
    private final Long userId;

    public PhotoReviewReplyLikeNotFoundException(Long replyId, Long userId) {
        super(String.format("대댓글 좋아요를 찾을 수 없습니다. ReplyId: %d, UserId: %d", replyId, userId));
        this.replyId = replyId;
        this.userId = userId;
    }
}
