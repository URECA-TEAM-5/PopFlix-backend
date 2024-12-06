package com.popflix.domain.review.controller;

import com.popflix.domain.review.dto.*;
import com.popflix.domain.review.service.ReviewService;
import com.popflix.global.util.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @Valid @RequestBody ReviewPostDto requestDto) {
        return ResponseEntity.ok(reviewService.createReview(requestDto));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDetailResponseDto> getReview(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByMovie(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getReviewsByMovieId(movieId));
    }

    @GetMapping("/movie/{movieId}/likes")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByMovieOrderByLikes(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(reviewService.getReviewsByMovieIdOrderByLikes(movieId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewListResponseDto>> getReviewsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewPatchDto requestDto) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, requestDto));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Void> likeReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.likeReview(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<Void> unlikeReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.unlikeReview(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/movie/{movieId}/count")
    public ResponseEntity<?> getReviewCount(@PathVariable Long movieId) {
        long count = reviewService.getReviewCount(movieId);
        return ResponseEntity.ok(ApiUtil.success(count));
    }
}
