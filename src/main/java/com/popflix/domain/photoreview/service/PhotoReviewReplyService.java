package com.popflix.domain.photoreview.service;

import com.popflix.domain.photoreview.dto.PhotoReviewReplyPatchDto;
import com.popflix.domain.photoreview.dto.PhotoReviewReplyPostDto;
import com.popflix.domain.photoreview.dto.PhotoReviewReplyResponseDto;

import java.util.List;

public interface PhotoReviewReplyService {
    PhotoReviewReplyResponseDto createReply(PhotoReviewReplyPostDto requestDto);
    PhotoReviewReplyResponseDto updateReply(Long replyId, PhotoReviewReplyPatchDto requestDto);
    void deleteReply(Long replyId, Long userId);
    List<PhotoReviewReplyResponseDto> getRepliesByCommentId(Long commentId);
    void likeReply(Long replyId, Long userId);
    void unlikeReply(Long replyId, Long userId);
}
