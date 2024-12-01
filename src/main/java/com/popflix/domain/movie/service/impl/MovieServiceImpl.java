package com.popflix.domain.movie.service.impl;

import com.popflix.domain.movie.dto.*;
import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.entity.Rating;
import com.popflix.domain.movie.repository.MovieLikeRepository;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.movie.repository.RatingRepository;
import com.popflix.domain.movie.service.MovieService;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.storage.repository.MovieStorageRepository;
import com.popflix.domain.storage.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<GetMovieListResponseDto> getMovieListByKeyword(String keyword, Pageable pageable) {
        final Page<Movie> movies = movieRepository.findByKeyword(keyword, pageable);

        return movies.map(movie -> {
            Double averageRating = calculateAverageRating(movie.getId());
            return GetMovieListResponseDto.from(movie, averageRating);
        });
    }

    // 영화 조회(장르별)
    @Override
    public Page<GetMovieListResponseDto> getMovieListByGenre(String genre, Pageable pageable) {
        final Page<Movie> movies = movieRepository.findByGenre(genre, pageable);

        return movies.map(movie -> {
            Double averageRating = calculateAverageRating(movie.getId());
            return GetMovieListResponseDto.from(movie, averageRating);
        });
    }

    // 영화 조회(전체)
    @Override
    public Page<GetMovieListResponseDto> getAllMovies(Pageable pageable) {
        final Page<Movie> movies = movieRepository.findAllMovieInfo(pageable);

        return movies.map(movie -> {
            Double averageRating = calculateAverageRating(movie.getId());
            return GetMovieListResponseDto.from(movie, averageRating);
        });
    }

    private Double calculateAverageRating(Long movieId) {
        Double averageRating = ratingRepository.findAverageRatingByMovieId(movieId);
        return averageRating != null ? averageRating : 0.0;
    }

    // 상세 조회
    public GetDetailsResponseDto getMovieDetails(Long movieId, Long userId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        Double averageRating = calculateAverageRating(movie.getId());

        Boolean likedByUser = (userId != null)
                ? movieLikeRepository.existsByMovie_IdAndUser_UserIdAndIsLiked(movieId, userId, true)
                : null;

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
}
