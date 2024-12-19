package com.popflix.domain.photoreview.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popflix.domain.photoreview.dto.*;
import com.popflix.domain.photoreview.service.PhotoReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photo-reviews")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PhotoReviewController {
    private final PhotoReviewService photoReviewService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> createPhotoReview(
            @RequestPart("data") String dataString,
            @RequestPart(value = "reviewImage", required = true) MultipartFile reviewImage) {
        log.info("포토리뷰 생성 요청 - 데이터: {}", dataString);
        log.info("포토리뷰 생성 요청 - 이미지 파일명: {}", reviewImage.getOriginalFilename());

        try {
            PhotoReviewPostDto requestDto = objectMapper.readValue(dataString, PhotoReviewPostDto.class);
            requestDto.setReviewImage(reviewImage);

            PhotoReviewResponseDto response = photoReviewService.createPhotoReview(requestDto);
            log.info("포토리뷰 생성 완료 - ID: {}", response.getReviewId());

            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            log.error("포토리뷰 생성 실패 - JSON 파싱 에러: {}", e.getMessage());
            throw new RuntimeException("잘못된 요청 데이터 형식입니다", e);
        }
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