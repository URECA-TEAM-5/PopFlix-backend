package com.popflix.domain.review.service.impl;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.notification.enums.NotificationType;
import com.popflix.domain.notification.event.dto.ReviewCreatedEvent;
import com.popflix.domain.review.dto.*;
import com.popflix.domain.review.entity.Comment;
import com.popflix.domain.review.entity.Review;
import com.popflix.domain.review.entity.ReviewLike;
import com.popflix.domain.review.exception.DuplicateReviewException;
import com.popflix.domain.review.exception.ReviewLikeNotFoundException;
import com.popflix.domain.review.exception.ReviewNotFoundException;
import com.popflix.domain.review.exception.UnauthorizedReviewAccessException;
import com.popflix.domain.review.repository.CommentLikeRepository;
import com.popflix.domain.review.repository.ReviewLikeRepository;
import com.popflix.domain.review.repository.ReviewRepository;
import com.popflix.domain.review.service.ReviewService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ReviewResponseDto createReview(ReviewPostDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        Movie movie = movieRepository.findById(requestDto.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException(requestDto.getMovieId()));

        validateNotExistReview(requestDto.getMovieId(), requestDto.getUserId());

        Review review = Review.builder()
                .review(requestDto.getReview())
                .movie(movie)
                .user(user)
                .build();

        Review savedReview = reviewRepository.save(review);

        publishReviewCreatedEvent(savedReview);

        return convertToReviewResponse(savedReview);
    }

    @Override
    public ReviewDetailResponseDto getReview(Long reviewId) {
        Review review = findReviewById(reviewId);
        return convertToReviewDetailResponse(review);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByMovieId(Long movieId) {
        List<Review> reviews = reviewRepository.findAllByMovieId(movieId);
        return reviews.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDto> getReviewsByMovieIdOrderByLikes(Long movieId) {
        List<Review> reviews = reviewRepository.findAllByOrderByLikesDesc();
        return reviews.stream()
                .map(this::convertToReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewListResponseDto> getReviewsByUserId(Long userId) {
        validateUser(userId);
        List<Review> reviews = reviewRepository.findAllByUserId(userId);
        return reviews.stream()
                .map(this::convertToReviewListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, ReviewPatchDto requestDto) {
        Review review = findReviewById(reviewId);
        review.updateReview(requestDto.getReview());
        return convertToReviewResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = findReviewById(reviewId);
        validateReviewOwner(review, userId);
        review.delete();
    }

    @Override
    @Transactional
    public void likeReview(Long reviewId, Long userId) {
        Review review = findReviewById(reviewId);
        validateUser(userId);

        ReviewLike reviewLike = getOrCreateReviewLike(review, userId);
        saveOrToggleReviewLike(reviewLike);
    }

    @Override
    @Transactional
    public void unlikeReview(Long reviewId, Long userId) {
        ReviewLike reviewLike = findReviewLike(reviewId, userId);
        reviewLike.toggleLike();
    }

    private Review findReviewById(Long reviewId) {
        return reviewRepository.findActiveById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateNotExistReview(Long movieId, Long userId) {
        if (reviewRepository.existsByMovieIdAndUserId(movieId, userId)) {
            throw new DuplicateReviewException("이미 해당 영화에 대한 리뷰가 존재합니다.");
        }
    }

    private void validateReviewOwner(Review review, Long userId) {
        if (!review.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedReviewAccessException("리뷰를 삭제할 권한이 없습니다.");
        }
    }

    private ReviewLike getOrCreateReviewLike(Review review, Long userId) {
        return reviewLikeRepository.findActiveByReviewIdAndUserId(review.getReviewId(), userId)
                .orElseGet(() -> ReviewLike.builder()
                        .review(review)
                        .user(userRepository.getReferenceById(userId))
                        .build());
    }

    private void saveOrToggleReviewLike(ReviewLike reviewLike) {
        if (reviewLike.getReviewLikeId() == null) {
            reviewLikeRepository.save(reviewLike);
        } else if (!reviewLike.getReviewLike()) {
            reviewLike.toggleLike();
        }
    }

    private ReviewLike findReviewLike(Long reviewId, Long userId) {
        return reviewLikeRepository.findActiveByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ReviewLikeNotFoundException(reviewId, userId));
    }

    private ReviewResponseDto convertToReviewResponse(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .review(review.getReview())
                .movie(convertToMovieInfo(review.getMovie()))
                .user(convertToUserInfo(review.getUser()))
                .createdAt(review.getCreateAt())
                .likeCount(reviewLikeRepository.countByReviewId(review.getReviewId()))
                .commentCount(review.getComments().size())
                .isHidden(review.getIsHidden())
                .build();
    }

    private ReviewDetailResponseDto convertToReviewDetailResponse(Review review) {
        return ReviewDetailResponseDto.builder()
                .reviewId(review.getReviewId())
                .review(review.getReview())
                .movie(convertToMovieInfo(review.getMovie()))
                .user(convertToUserInfo(review.getUser()))
                .createdAt(review.getCreateAt())
                .updatedAt(review.getUpdateAt())
                .likeCount(reviewLikeRepository.countByReviewId(review.getReviewId()))
                .comments(review.getComments().stream()
                        .filter(comment -> !comment.getIsDeleted())  // 삭제되지 않은 댓글만 포함
                        .map(this::convertToCommentResponse)
                        .collect(Collectors.toList()))
                .isHidden(review.getIsHidden())
                .build();
    }

    // CommentResponseDto 변환 메서드 추가
    private CommentResponseDto convertToCommentResponse(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .comment(comment.getComment())
                .user(convertToCommentUserInfo(comment.getUser()))
                .createdAt(comment.getCreateAt())
                .likeCount(commentLikeRepository.countByCommentId(comment.getCommentId()))
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

    private ReviewListResponseDto convertToReviewListResponse(Review review) {
        return ReviewListResponseDto.builder()
                .reviewId(review.getReviewId())
                .review(review.getReview())
                .movieTitle(review.getMovie().getTitle())
                .createdAt(review.getCreateAt())
                .likeCount(reviewLikeRepository.countByReviewId(review.getReviewId()))
                .commentCount(review.getComments().size())
                .build();
    }

    private ReviewResponseDto.MovieInfo convertToMovieInfo(Movie movie) {
        return ReviewResponseDto.MovieInfo.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .build();
    }

    private ReviewResponseDto.UserInfo convertToUserInfo(User user) {
        return ReviewResponseDto.UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImage())
                .build();
    }

    private void publishReviewCreatedEvent(Review review) {
        ReviewCreatedEvent event = ReviewCreatedEvent.builder()
                .reviewId(review.getReviewId())
                .movieId(review.getMovie().getId())
                .movieTitle(review.getMovie().getTitle())
                .genreIds(review.getMovie().getMovieGenres().stream()
                        .map(mg -> mg.getGenre().getId())
                        .collect(Collectors.toList()))
                .type(NotificationType.NEW_REVIEW)
                .reviewer(review.getUser())
                .reviewerNickname(review.getUser().getNickname())
                .reviewContent(review.getReview())
                .createdAt(review.getCreateAt())
                .build();

        eventPublisher.publishEvent(event);
    }

    @Override
    public long getReviewCount(Long movieId) {
        return reviewRepository.countByMovieId(movieId);
    }
}