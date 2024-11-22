package com.popflix.domain.review.controller;

import com.popflix.domain.review.dto.CommentPatchDto;
import com.popflix.domain.review.dto.CommentPostDto;
import com.popflix.domain.review.dto.CommentResponseDto;
import com.popflix.domain.review.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @Valid @RequestBody CommentPostDto requestDto) {
        return ResponseEntity.ok(commentService.createComment(requestDto));
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByReview(
            @PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentsByReviewId(reviewId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentPatchDto requestDto) {
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
}
