package com.popflix.domain.photoreview.service;

import com.popflix.domain.photoreview.dto.PhotoReviewCommentPatchDto;
import com.popflix.domain.photoreview.dto.PhotoReviewCommentPostDto;
import com.popflix.domain.photoreview.dto.PhotoReviewCommentResponseDto;

import java.util.List;

public interface PhotoReviewCommentService {
    PhotoReviewCommentResponseDto createComment(PhotoReviewCommentPostDto requestDto);
    PhotoReviewCommentResponseDto updateComment(Long commentId, PhotoReviewCommentPatchDto requestDto);
    void deleteComment(Long commentId, Long userId);
    List<PhotoReviewCommentResponseDto> getCommentsByReviewId(Long reviewId);
    List<PhotoReviewCommentResponseDto> getCommentsByUserId(Long userId);
    void likeComment(Long commentId, Long userId);
    void unlikeComment(Long commentId, Long userId);
}
