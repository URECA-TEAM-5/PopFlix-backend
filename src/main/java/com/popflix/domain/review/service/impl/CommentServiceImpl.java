package com.popflix.domain.review.service.impl;

import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.review.dto.CommentPatchDto;
import com.popflix.domain.review.dto.CommentPostDto;
import com.popflix.domain.review.dto.CommentResponseDto;
import com.popflix.domain.review.entity.Comment;
import com.popflix.domain.review.entity.CommentLike;
import com.popflix.domain.review.entity.Review;
import com.popflix.domain.review.exception.CommentLikeNotFoundException;
import com.popflix.domain.review.exception.CommentNotFoundException;
import com.popflix.domain.review.exception.ReviewNotFoundException;
import com.popflix.domain.review.exception.UnauthorizedReviewAccessException;
import com.popflix.domain.review.repository.CommentLikeRepository;
import com.popflix.domain.review.repository.CommentRepository;
import com.popflix.domain.review.repository.ReviewRepository;
import com.popflix.domain.review.service.CommentService;
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
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public CommentResponseDto createComment(CommentPostDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        Review review = reviewRepository.findActiveById(requestDto.getReviewId())
                .orElseThrow(() -> new ReviewNotFoundException(requestDto.getReviewId()));

        Comment comment = Comment.builder()
                .comment(requestDto.getComment())
                .review(review)
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return convertToCommentResponse(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentPatchDto requestDto) {
        Comment comment = findCommentById(commentId);
        comment.updateComment(requestDto.getComment());
        return convertToCommentResponse(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);
        comment.delete();
    }

    @Override
    public List<CommentResponseDto> getCommentsByReviewId(Long reviewId) {
        List<Comment> comments = commentRepository.findAllByReviewId(reviewId);
        return comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponseDto> getCommentsByUserId(Long userId) {
        validateUser(userId);
        List<Comment> comments = commentRepository.findAllByUserId(userId);
        return comments.stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        validateUser(userId);

        CommentLike commentLike = getOrCreateCommentLike(comment, userId);
        saveOrToggleCommentLike(commentLike);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        CommentLike commentLike = findCommentLike(commentId, userId);
        commentLike.toggleLike();
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findActiveById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedReviewAccessException("댓글을 삭제할 권한이 없습니다.");
        }
    }

    private CommentLike getOrCreateCommentLike(Comment comment, Long userId) {
        return commentLikeRepository.findActiveByCommentIdAndUserId(comment.getCommentId(), userId)
                .orElseGet(() -> CommentLike.builder()
                        .comment(comment)
                        .user(userRepository.getReferenceById(userId))
                        .build());
    }

    private void saveOrToggleCommentLike(CommentLike commentLike) {
        if (commentLike.getCommentLikeId() == null) {
            commentLikeRepository.save(commentLike);
        } else if (!commentLike.getCommentLike()) {
            commentLike.toggleLike();
        }
    }

    private CommentLike findCommentLike(Long commentId, Long userId) {
        return commentLikeRepository.findActiveByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new CommentLikeNotFoundException(commentId, userId));
    }

    private CommentResponseDto convertToCommentResponse(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .user(convertToCommentUserInfo(comment.getUser()))
                .createdAt(comment.getCreateAt())
                .likeCount(commentLikeRepository.countByCommentId(comment.getCommentId()))
                .isLiked(false)
                .isHidden(comment.getIsHidden())
                .build();
    }

    private CommentResponseDto.UserInfo convertToCommentUserInfo(User user) {
        return CommentResponseDto.UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImage())
                .build();
    }
}