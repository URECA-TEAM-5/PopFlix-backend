package com.popflix.domain.photoreview.controller;

import com.popflix.domain.photoreview.dto.PhotoReviewCommentPatchDto;
import com.popflix.domain.photoreview.dto.PhotoReviewCommentPostDto;
import com.popflix.domain.photoreview.dto.PhotoReviewCommentResponseDto;
import com.popflix.domain.photoreview.service.PhotoReviewCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photo-review-comments")
@RequiredArgsConstructor
@Validated
public class PhotoReviewCommentController {
    private final PhotoReviewCommentService commentService;

    @PostMapping
    public ResponseEntity<PhotoReviewCommentResponseDto> createComment(
            @Valid @RequestBody PhotoReviewCommentPostDto requestDto) {
        return ResponseEntity.ok(commentService.createComment(requestDto));
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<PhotoReviewCommentResponseDto>> getCommentsByReview(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentsByReviewId(reviewId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PhotoReviewCommentResponseDto>> getCommentsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<PhotoReviewCommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody PhotoReviewCommentPatchDto requestDto) {
        return ResponseEntity.ok(commentService.updateComment(commentId, requestDto));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        commentService.likeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) {
        commentService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/review/{reviewId}/likes")
    public ResponseEntity<List<PhotoReviewCommentResponseDto>> getCommentsByReviewOrderByLikes(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentsByReviewIdOrderByLikes(reviewId));
    }
}
