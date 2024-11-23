package com.popflix.domain.photoreview.controller;

import com.popflix.domain.photoreview.dto.*;
import com.popflix.domain.photoreview.service.PhotoReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photo-reviews")
@RequiredArgsConstructor
@Validated
public class PhotoReviewController {
    private final PhotoReviewService photoReviewService;

    @PostMapping
    public ResponseEntity<PhotoReviewResponseDto> createPhotoReview(
            @Valid @RequestBody PhotoReviewPostDto requestDto) {
        return ResponseEntity.ok(photoReviewService.createPhotoReview(requestDto));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<PhotoReviewDetailResponseDto> getPhotoReview(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(photoReviewService.getPhotoReview(reviewId));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<PhotoReviewResponseDto>> getPhotoReviewsByMovie(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(photoReviewService.getPhotoReviewsByMovieId(movieId));
    }

    @GetMapping("/movie/{movieId}/likes")
    public ResponseEntity<List<PhotoReviewResponseDto>> getPhotoReviewsByMovieOrderByLikes(
            @PathVariable Long movieId) {
        return ResponseEntity.ok(photoReviewService.getPhotoReviewsByMovieIdOrderByLikes(movieId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PhotoReviewListResponseDto>> getPhotoReviewsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(photoReviewService.getPhotoReviewsByUserId(userId));
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<PhotoReviewResponseDto> updatePhotoReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody PhotoReviewPatchDto requestDto) {
        return ResponseEntity.ok(photoReviewService.updatePhotoReview(reviewId, requestDto));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deletePhotoReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        photoReviewService.deletePhotoReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Void> likePhotoReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        photoReviewService.likePhotoReview(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<Void> unlikePhotoReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        photoReviewService.unlikePhotoReview(reviewId, userId);
        return ResponseEntity.ok().build();
    }
}
