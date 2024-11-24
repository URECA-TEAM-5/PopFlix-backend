package com.popflix.domain.photoreview.controller;

import com.popflix.domain.photoreview.dto.PhotoReviewReplyPatchDto;
import com.popflix.domain.photoreview.dto.PhotoReviewReplyPostDto;
import com.popflix.domain.photoreview.dto.PhotoReviewReplyResponseDto;
import com.popflix.domain.photoreview.service.PhotoReviewReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/photo-review-replies")
@RequiredArgsConstructor
@Validated
public class PhotoReviewReplyController {
    private final PhotoReviewReplyService replyService;

    @PostMapping
    public ResponseEntity<PhotoReviewReplyResponseDto> createReply(
            @Valid @RequestBody PhotoReviewReplyPostDto requestDto) {
        return ResponseEntity.ok(replyService.createReply(requestDto));
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<PhotoReviewReplyResponseDto>> getRepliesByComment(
            @PathVariable Long commentId) {
        return ResponseEntity.ok(replyService.getRepliesByCommentId(commentId));
    }

    @PatchMapping("/{replyId}")
    public ResponseEntity<PhotoReviewReplyResponseDto> updateReply(
            @PathVariable Long replyId,
            @Valid @RequestBody PhotoReviewReplyPatchDto requestDto) {
        return ResponseEntity.ok(replyService.updateReply(replyId, requestDto));
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long replyId,
            @RequestParam Long userId) {
        replyService.deleteReply(replyId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{replyId}/like")
    public ResponseEntity<Void> likeReply(
            @PathVariable Long replyId,
            @RequestParam Long userId) {
        replyService.likeReply(replyId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{replyId}/like")
    public ResponseEntity<Void> unlikeReply(
            @PathVariable Long replyId,
            @RequestParam Long userId) {
        replyService.unlikeReply(replyId, userId);
        return ResponseEntity.ok().build();
    }
}
