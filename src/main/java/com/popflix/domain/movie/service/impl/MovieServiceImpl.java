package com.popflix.domain.movie.service.impl;

import com.popflix.domain.movie.dto.*;
import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.entity.Rating;
import com.popflix.domain.movie.repository.MovieLikeRepository;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.movie.repository.RatingRepository;
import com.popflix.domain.movie.service.MovieService;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;
    private final MovieLikeRepository movieLikeRepository;

    // 별점 조회 및 평균 별점 계산
    @Override
    public GetMovieRatingResponseDto getMovieRatings(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        // 영화의 평점 평균 계산
        Double averageRating = ratingRepository.findAverageRatingByMovieId(movieId);

        Double roundedRating = averageRating != null ? averageRating : 0.0;

        List<Rating> ratings = ratingRepository.findAllRatingsByMovieId(movieId);

        return GetMovieRatingResponseDto.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .averageRating(roundedRating)
                .ratings(ratings.stream()
                        .map(r -> GetRatingResponseDto.builder()
                                .userId(r.getUser().getUserId())
                                .rating(r.getRating())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    // 영화 검색(키워드)
    @Override
    public Page<GetMovieListResponseDto> getMovieListByKeyword(String keyword, Pageable pageable, Long userId) {
        Page<Movie> movies = movieRepository.findByKeyword(keyword, pageable);

        return movies.map(movie -> {
            Double averageRating = calculateAverageRating(movie.getId());
            Boolean isLiked = getIsLiked(userId, movie.getId());
            return GetMovieListResponseDto.from(movie, averageRating, isLiked);
        });
    }

    // 영화 조회(장르별)
    @Override
    public Page<GetMovieListResponseDto> getMovieListByGenre(String genre, Pageable pageable, Long userId, String sort) {
        Sort sorting;
        if ("popular".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "likeCount"); // 평점 내림차순
        } else if ("newest".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "releaseDate"); // 출시일 내림차순
        } else {
            throw new IllegalArgumentException("Invalid sort type. Allowed values: 'popular', 'newest'. Provided: " + sort);
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);

        Page<Movie> movies = movieRepository.findByGenre(genre, sortedPageable);

        return movies.map(movie -> {
            Double averageRating = calculateAverageRating(movie.getId());
            Boolean isLiked = getIsLiked(userId, movie.getId());
            return GetMovieListResponseDto.from(movie, averageRating, isLiked);
        });
    }

    // 영화 조회(전체)
    @Override
    public Page<GetMovieListResponseDto> getAllMovies(Pageable pageable, Long userId, String sort) {
        Sort sorting = "popular".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.DESC, "likeCount")
                : Sort.by(Sort.Direction.DESC, "releaseDate");

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);
        Page<Movie> movies = movieRepository.findAllWithReleaseDateBeforeNow(sortedPageable);

        return movies.map(movie -> {
            Double averageRating = calculateAverageRating(movie.getId());
            Boolean isLiked = getIsLiked(userId, movie.getId());
            return GetMovieListResponseDto.from(movie, averageRating, isLiked);
        });
    }

    private Double calculateAverageRating(Long movieId) {
        Double averageRating = ratingRepository.findAverageRatingByMovieId(movieId);
        return averageRating != null ? averageRating : 0.0;
    }

    // 상세 조회 - 좋아요 상태 추가
    @Override
    public GetDetailsResponseDto getMovieDetails(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        Double averageRating = calculateAverageRating(movie.getId());
        Boolean likedByUser = getIsLiked(userId, movie.getId());

        List<SimpleDto> cast = movie.getMovieCasts()
                .stream()
                .map(movieCast -> SimpleDto.fromCast(movieCast.getCast()))
                .toList();

        List<SimpleDto> directors = movie.getMovieDirectors()
                .stream()
                .map(movieDirector -> SimpleDto.fromDirector(movieDirector.getDirector()))
                .toList();

        List<SimpleDto> genres = movie.getMovieGenres()
                .stream()
                .map(movieGenre -> SimpleDto.fromGenre(movieGenre.getGenre()))
                .toList();

        List<SimpleDto> reviewVideos = movie.getReviewVideos()
                .stream()
                .map(SimpleDto::fromReviewVideo)
                .toList();

        return GetDetailsResponseDto.from(movie, averageRating, likedByUser, cast, directors, genres, reviewVideos);
    }

    // 좋아요 여부 확인
    private Boolean getIsLiked(Long userId, Long movieId) {
        if (userId == null) {
            return false;
        }
        return movieLikeRepository.existsByMovie_IdAndUser_UserIdAndIsLiked(movieId, userId, true);
    }
}
