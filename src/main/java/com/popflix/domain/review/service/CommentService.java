package com.popflix.domain.review.service;

import com.popflix.domain.review.dto.CommentPatchDto;
import com.popflix.domain.review.dto.CommentPostDto;
import com.popflix.domain.review.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(CommentPostDto requestDto);
    CommentResponseDto updateComment(Long commentId, CommentPatchDto requestDto);
    void deleteComment(Long commentId, Long userId);
    List<CommentResponseDto> getCommentsByReviewId(Long reviewId);
    List<CommentResponseDto> getCommentsByUserId(Long userId);
    void likeComment(Long commentId, Long userId);
    void unlikeComment(Long commentId, Long userId);
}
