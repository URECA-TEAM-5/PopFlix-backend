package com.popflix.domain.movie.controller;

import com.popflix.domain.movie.dto.*;
import com.popflix.domain.movie.service.MovieApiService;
import com.popflix.domain.movie.service.MovieLikeService;
import com.popflix.domain.movie.service.MovieService;
import com.popflix.domain.movie.service.RatingService;
import com.popflix.global.util.ApiUtil;
import com.popflix.global.util.ApiUtil.ApiSuccess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
// cicd 테스트 - 3.1
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieApiService movieApiService;
    private final MovieService movieService;
    private final RatingService ratingService;
    private final MovieLikeService movieLikeService;

    // 영화 정보, 장르 정보 저장
    @GetMapping("/save")
    public String saveMovies() {
        movieApiService.saveMovies();
        return "Movies saved successfully!";
    }

    // 영화 별점 추가 및 수정
    @PostMapping("/rating")
    public ApiSuccess<?> addRating(@RequestBody @Valid AddRatingRequestDto request) {
        String message = ratingService.addOrUpdateRating(request.getUserId(), request.getMovieId(), request.getRating());
        return ApiUtil.success(message);
    }

    // 영화 별점 삭제
    @DeleteMapping("/rating")
    public ApiSuccess<?> deleteRating(@RequestParam Long userId, @RequestParam Long movieId) {
        String message = ratingService.deleteRating(userId, movieId);
        return ApiUtil.success(message);
    }

    // 영화 별점 조회
    @GetMapping("/{movieId}/ratings")
    public ApiSuccess<?> getMovieRatings(@PathVariable Long movieId) {
        GetMovieRatingResponseDto movieRatingResponse  = movieService.getMovieRatings(movieId);
        return ApiUtil.success(movieRatingResponse);
    }

    // 영화 좋아요 추가 & 취소
    @PostMapping("/{movieId}/like")
    public ApiSuccess<?> likeStatus(
            @PathVariable Long movieId,
            @RequestParam Long userId
    ) {
        boolean isLiked = movieLikeService.likeStatus(userId, movieId);
        String message = isLiked ? "좋아요를 추가했습니다." : "좋아요를 취소했습니다.";
        return ApiUtil.success(message);
    }

    // 영화 좋아요 상태 조회
    @GetMapping("/like")
    public ApiSuccess<?> getLikeStatus(
            @RequestParam Long movieId,
            @RequestParam Long userId
    ){
        Map<String, Boolean> likeStatus = movieLikeService.checkLikeStatus(movieId, userId);

        return ApiUtil.success(likeStatus);
    }

    // 영화 목록 조회
    @GetMapping
    public ApiSuccess<?> getMovieList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "userId", required = false) Long userId,
            Pageable pageable) {

        Page<GetMovieListResponseDto> movieList;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드 기반 검색
            movieList = movieService.getMovieListByKeyword(keyword, pageable, userId);
        } else if (genre != null && !genre.trim().isEmpty()) {
            // 장르별 영화 검색
            movieList = movieService.getMovieListByGenre(genre, pageable, userId);
        } else {
            // 전체 영화 조회
            movieList = movieService.getAllMovies(pageable, userId);
        }

        return ApiUtil.success(movieList);
    }

    // 영화 상세 조회
    @GetMapping("/{movieId}/details")
    public ApiSuccess<GetDetailsResponseDto> getMovieDetails(
            @PathVariable Long movieId,
            @RequestParam(value = "userId", required = false) Long userId) {

        GetDetailsResponseDto movieDetails = movieService.getMovieDetails(movieId, userId);
        return ApiUtil.success(movieDetails);
    }

    // 로그인하지 않은 유저에게 tmdb 이용한 인기 영화 제공
    @GetMapping("/recommended/popular")
    public ApiSuccess<?> getPopularMovies() {
        List<GetRecommendedMovieResponseDto> popularMovies = movieApiService.getRecommendedMovies();
        return ApiUtil.success(popularMovies);
    }

    // 로그인 한 유저에게 tmdb 이용한 장르 기반 추천 영화 제공
    @GetMapping("/recommended/genre/{userId}")
    public ApiSuccess<?> getMovieRecommendations(@PathVariable Long userId) {
        List<GetRecommendedMovieResponseDto> genreBasedMovies = movieApiService.getGenreBasedMovies(userId);
        return ApiUtil.success(genreBasedMovies);
    }


}
