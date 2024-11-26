package com.popflix.domain.photoreview.service;

import com.popflix.domain.photoreview.dto.*;

import java.util.List;

public interface PhotoReviewService {
    PhotoReviewResponseDto createPhotoReview(PhotoReviewPostDto requestDto);
    PhotoReviewDetailResponseDto getPhotoReview(Long reviewId);
    List<PhotoReviewResponseDto> getPhotoReviewsByMovieId(Long movieId);
    List<PhotoReviewResponseDto> getPhotoReviewsByMovieIdOrderByLikes(Long movieId);
    List<PhotoReviewListResponseDto> getPhotoReviewsByUserId(Long userId);
    PhotoReviewResponseDto updatePhotoReview(Long reviewId, PhotoReviewPatchDto requestDto);
    void deletePhotoReview(Long reviewId, Long userId);
    void likePhotoReview(Long reviewId, Long userId);
    void unlikePhotoReview(Long reviewId, Long userId);
}
