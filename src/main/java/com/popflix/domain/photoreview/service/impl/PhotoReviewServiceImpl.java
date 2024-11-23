package com.popflix.domain.photoreview.service.impl;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.photoreview.dto.*;
import com.popflix.domain.photoreview.entity.PhotoReview;
import com.popflix.domain.photoreview.entity.PhotoReviewLike;
import com.popflix.domain.photoreview.exception.PhotoReviewLikeNotFoundException;
import com.popflix.domain.photoreview.exception.PhotoReviewNotFoundException;
import com.popflix.domain.photoreview.exception.UnauthorizedPhotoReviewAccessException;
import com.popflix.domain.photoreview.repository.PhotoReviewLikeRepository;
import com.popflix.domain.photoreview.repository.PhotoReviewRepository;
import com.popflix.domain.photoreview.service.PhotoReviewService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PhotoReviewServiceImpl implements PhotoReviewService {
    private final PhotoReviewRepository photoReviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final PhotoReviewLikeRepository photoReviewLikeRepository;

    @Override
    @Transactional
    public PhotoReviewResponseDto createPhotoReview(PhotoReviewPostDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(requestDto.getUserId()));

        Movie movie = movieRepository.findById(requestDto.getMovieId())
                .orElseThrow(() -> new MovieNotFoundException(requestDto.getMovieId()));

        PhotoReview photoReview = PhotoReview.builder()
                .review(requestDto.getReview())
                .reviewImage(requestDto.getReviewImage())
                .movie(movie)
                .user(user)
                .build();

        PhotoReview savedPhotoReview = photoReviewRepository.save(photoReview);
        return convertToPhotoReviewResponse(savedPhotoReview);
    }

    @Override
    public PhotoReviewDetailResponseDto getPhotoReview(Long reviewId) {
        PhotoReview photoReview = findPhotoReviewById(reviewId);
        return convertToPhotoReviewDetailResponse(photoReview);
    }

    @Override
    public List<PhotoReviewResponseDto> getPhotoReviewsByMovieId(Long movieId) {
        List<PhotoReview> photoReviews = photoReviewRepository.findAllByMovieId(movieId);
        return photoReviews.stream()
                .map(this::convertToPhotoReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoReviewResponseDto> getPhotoReviewsByMovieIdOrderByLikes(Long movieId) {
        Page<PhotoReview> photoReviews = photoReviewRepository
                .findPageByMovieIdOrderByLikesDesc(movieId, PageRequest.of(0, 10));
        return photoReviews.stream()
                .map(this::convertToPhotoReviewResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoReviewListResponseDto> getPhotoReviewsByUserId(Long userId) {
        validateUser(userId);
        List<PhotoReview> photoReviews = photoReviewRepository.findAllByUserId(userId);
        return photoReviews.stream()
                .map(this::convertToPhotoReviewListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PhotoReviewResponseDto updatePhotoReview(Long reviewId, PhotoReviewPatchDto requestDto) {
        PhotoReview photoReview = findPhotoReviewById(reviewId);

        photoReview.updateReview(requestDto.getReview());
        if (requestDto.getReviewImage() != null) {
            photoReview.updateImage(requestDto.getReviewImage());
        }

        return convertToPhotoReviewResponse(photoReview);
    }

    @Override
    @Transactional
    public void deletePhotoReview(Long reviewId, Long userId) {
        PhotoReview photoReview = findPhotoReviewById(reviewId);
        validatePhotoReviewOwner(photoReview, userId);
        photoReview.delete();
    }

    @Override
    @Transactional
    public void likePhotoReview(Long reviewId, Long userId) {
        PhotoReview photoReview = findPhotoReviewById(reviewId);
        validateUser(userId);

        PhotoReviewLike photoReviewLike = getOrCreatePhotoReviewLike(photoReview, userId);
        saveOrTogglePhotoReviewLike(photoReviewLike);
    }

    @Override
    @Transactional
    public void unlikePhotoReview(Long reviewId, Long userId) {
        PhotoReviewLike photoReviewLike = findPhotoReviewLike(reviewId, userId);
        photoReviewLike.toggleLike();
    }

    // Private helper methods
    private PhotoReview findPhotoReviewById(Long reviewId) {
        return photoReviewRepository.findActiveById(reviewId)
                .orElseThrow(() -> new PhotoReviewNotFoundException(reviewId));
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validatePhotoReviewOwner(PhotoReview photoReview, Long userId) {
        if (!photoReview.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedPhotoReviewAccessException("포토리뷰를 삭제할 권한이 없습니다.");
        }
    }

    private PhotoReviewLike getOrCreatePhotoReviewLike(PhotoReview photoReview, Long userId) {
        return photoReviewLikeRepository
                .findActiveByReviewIdAndUserId(photoReview.getReviewId(), userId)
                .orElseGet(() -> PhotoReviewLike.builder()
                        .photoReview(photoReview)
                        .user(userRepository.getReferenceById(userId))
                        .build());
    }

    private void saveOrTogglePhotoReviewLike(PhotoReviewLike photoReviewLike) {
        if (photoReviewLike.getReviewLikeId() == null) {
            photoReviewLikeRepository.save(photoReviewLike);
        } else if (!photoReviewLike.getReviewLike()) {
            photoReviewLike.toggleLike();
        }
    }

    private PhotoReviewLike findPhotoReviewLike(Long reviewId, Long userId) {
        return photoReviewLikeRepository
                .findActiveByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new PhotoReviewLikeNotFoundException(reviewId, userId));
    }

    // Convert to DTO methods
    private PhotoReviewResponseDto convertToPhotoReviewResponse(PhotoReview photoReview) {
        String profileImageBase64 = photoReview.getUser().getProfileImage() != null ?
                Base64.getEncoder().encodeToString(photoReview.getUser().getProfileImage()) : null;

        return PhotoReviewResponseDto.builder()
                .reviewId(photoReview.getReviewId())
                .review(photoReview.getReview())
                .reviewImageUrl(Base64.getEncoder().encodeToString(photoReview.getReviewImage()))
                .movie(convertToMovieInfo(photoReview.getMovie()))
                .user(convertToUserInfo(photoReview.getUser()))
                .createdAt(photoReview.getCreateAt())
                .likeCount(photoReviewLikeRepository.countByReviewId(photoReview.getReviewId()))
                .commentCount(photoReview.getComments().size())
                .isHidden(photoReview.getIsHidden())
                .build();
    }

    private PhotoReviewDetailResponseDto convertToPhotoReviewDetailResponse(PhotoReview photoReview) {
        return PhotoReviewDetailResponseDto.builder()
                .reviewId(photoReview.getReviewId())
                .review(photoReview.getReview())
                .reviewImageUrl(Base64.getEncoder().encodeToString(photoReview.getReviewImage()))
                .movie(convertToMovieInfo(photoReview.getMovie()))
                .user(convertToUserInfo(photoReview.getUser()))
                .createdAt(photoReview.getCreateAt())
                .updatedAt(photoReview.getUpdateAt())
                .likeCount(photoReviewLikeRepository.countByReviewId(photoReview.getReviewId()))
                .isHidden(photoReview.getIsHidden())
                .build();
    }

    private PhotoReviewListResponseDto convertToPhotoReviewListResponse(PhotoReview photoReview) {
        return PhotoReviewListResponseDto.builder()
                .reviewId(photoReview.getReviewId())
                .review(photoReview.getReview())
                .reviewImageUrl(Base64.getEncoder().encodeToString(photoReview.getReviewImage()))
                .movieTitle(photoReview.getMovie().getTitle())
                .createdAt(photoReview.getCreateAt())
                .likeCount(photoReviewLikeRepository.countByReviewId(photoReview.getReviewId()))
                .commentCount(photoReview.getComments().size())
                .build();
    }

    private PhotoReviewResponseDto.MovieInfo convertToMovieInfo(Movie movie) {
        return PhotoReviewResponseDto.MovieInfo.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .build();
    }

    private PhotoReviewResponseDto.UserInfo convertToUserInfo(User user) {
        String profileImageBase64 = user.getProfileImage() != null ?
                Base64.getEncoder().encodeToString(user.getProfileImage()) : null;

        return PhotoReviewResponseDto.UserInfo.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImageUrl(profileImageBase64)
                .build();
    }
}
