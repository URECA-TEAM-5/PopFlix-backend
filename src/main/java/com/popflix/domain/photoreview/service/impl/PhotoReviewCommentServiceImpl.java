package com.popflix.domain.photoreview.service.impl;

import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.photoreview.dto.PhotoReviewCommentPatchDto;
import com.popflix.domain.photoreview.dto.PhotoReviewCommentPostDto;
import com.popflix.domain.photoreview.dto.PhotoReviewCommentResponseDto;
import com.popflix.domain.photoreview.entity.PhotoReview;
import com.popflix.domain.photoreview.entity.PhotoReviewComment;
import com.popflix.domain.photoreview.entity.PhotoReviewCommentLike;
import com.popflix.domain.photoreview.exception.PhotoReviewCommentLikeNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewCommentNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewNotFoundException;
import com.popflix.domain.photoreview.exception.UnauthorizedPhotoReviewAccessException;
import com.popflix.domain.photoreview.repository.PhotoReviewCommentLikeRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewCommentRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewRepository;
import com.popflix.domain.photoreview.service.PhotoReviewCommentService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotoReviewCommentServiceImpl implements PhotoReviewCommentService {
    private final PhotoReviewCommentRepository commentRepository;
    private final PhotoReviewRepository photoReviewRepository;
    private final UserRepository userRepository;
    private final PhotoReviewCommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public PhotoReviewCommentResponseDto createComment(PhotoReviewCommentPostDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        PhotoReview photoReview = photoReviewRepository.findActiveById(requestDto.getReviewId())
                .orElseThrow(() -> new PhotoReviewNotFoundException(requestDto.getReviewId()));

        PhotoReviewComment comment = PhotoReviewComment.builder()
                .comment(requestDto.getComment())
                .photoReview(photoReview)
                .user(user)
                .build();

        PhotoReviewComment savedComment = commentRepository.save(comment);
        return convertToCommentResponse(savedComment);
    }

    @Override
    @Transactional
    public PhotoReviewCommentResponseDto updateComment(Long commentId, PhotoReviewCommentPatchDto requestDto) {
        PhotoReviewComment comment = findCommentById(commentId);
        comment.updateComment(requestDto.getComment());
        return convertToCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        PhotoReviewComment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);
        comment.delete();
    }

    @Override
    public List<PhotoReviewCommentResponseDto> getCommentsByReviewId(Long reviewId) {
        List<PhotoReviewComment> comments = commentRepository.findAllByReviewId(reviewId);
        return comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoReviewCommentResponseDto> getCommentsByUserId(Long userId) {
        validateUser(userId);
        List<PhotoReviewComment> comments = commentRepository.findAllByUserId(userId);
        return comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        PhotoReviewComment comment = findCommentById(commentId);
        validateUser(userId);

        PhotoReviewCommentLike commentLike = getOrCreateCommentLike(comment, userId);
        saveOrToggleCommentLike(commentLike);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        PhotoReviewCommentLike commentLike = findCommentLike(commentId, userId);
        commentLike.toggleLike();
    }

    // Private helper methods
    private PhotoReviewComment findCommentById(Long commentId) {
        return commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new PhotoReviewCommentNotFoundException(commentId));
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateCommentOwner(PhotoReviewComment comment, Long userId) {
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedPhotoReviewAccessException("댓글을 삭제할 권한이 없습니다.");
        }
    }

    private PhotoReviewCommentLike getOrCreateCommentLike(PhotoReviewComment comment, Long userId) {
        return commentLikeRepository.findActiveByCommentIdAndUserId(comment.getCommentId(), userId)
                .orElseGet(() -> PhotoReviewCommentLike.builder()
                        .comment(comment)
                        .user(userRepository.getReferenceById(userId))
                        .build());
    }

    private void saveOrToggleCommentLike(PhotoReviewCommentLike commentLike) {
        if (commentLike.getCommentLikeId() == null) {
            commentLikeRepository.save(commentLike);
        } else if (!commentLike.getCommentLike()) {
            commentLike.toggleLike();
        }
    }

    private PhotoReviewCommentLike findCommentLike(Long commentId, Long userId) {
        return commentLikeRepository.findActiveByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new PhotoReviewCommentLikeNotFoundException(commentId, userId));
    }

    private PhotoReviewCommentResponseDto convertToCommentResponse(PhotoReviewComment comment) {
        return PhotoReviewCommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .user(convertToCommentUserInfo(comment.getUser()))
                .createdAt(comment.getCreateAt())
                .likeCount(commentLikeRepository.countByCommentId(comment.getCommentId()))
                .isLiked(false)
                .isHidden(comment.getIsHidden())
                .build();
    }

    private PhotoReviewCommentResponseDto.UserInfo convertToCommentUserInfo(User user) {
        return PhotoReviewCommentResponseDto.UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImage())
                .build();
    }

    @Override
    public List<PhotoReviewCommentResponseDto> getCommentsByReviewIdOrderByLikes(Long reviewId) {
        List<PhotoReviewComment> comments = commentRepository.findAllByReviewIdOrderByLikesDesc(reviewId);
        return comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }
}