package com.popflix.domain.review.service;

import com.popflix.domain.review.dto.*;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(ReviewPostDto requestDto);
    ReviewDetailResponseDto getReview(Long reviewId);
    List<ReviewResponseDto> getReviewsByMovieId(Long movieId);
    List<ReviewResponseDto> getReviewsByMovieIdOrderByLikes(Long movieId);
    List<ReviewListResponseDto> getReviewsByUserId(Long userId);
    ReviewResponseDto updateReview(Long reviewId, ReviewPatchDto requestDto);
    void deleteReview(Long reviewId, Long userId);
    void likeReview(Long reviewId, Long userId);
    void unlikeReview(Long reviewId, Long userId);
    long getReviewCount(Long movieId);
}
