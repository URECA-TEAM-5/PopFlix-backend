package com.popflix.domain.movie.service;

import com.popflix.domain.movie.dto.GetRecommendedMovieResponseDto;

import java.util.List;

public interface MovieApiService {
    void saveMovies();

    List<GetRecommendedMovieResponseDto> getGenreBasedMovies(Long userId);

    List<GetRecommendedMovieResponseDto> getRecommendedMovies();
}
