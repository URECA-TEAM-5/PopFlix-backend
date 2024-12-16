package com.popflix.domain.movie.service;

import com.popflix.domain.movie.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieService {
    GetMovieRatingResponseDto getMovieRatings(Long movieId);

    Page<GetSearchResultResponseDto> getMovieListByKeyword(String keyword, Pageable pageable, Long userId);

    Page<GetMovieListResponseDto> getMovieListByGenre(String genre, Pageable pageable, Long userId, String sort);

    Page<GetMovieListResponseDto> getAllMovies(Pageable pageable, Long userId, String sort);

    GetDetailsResponseDto getMovieDetails(Long movieId, Long userId);
}
