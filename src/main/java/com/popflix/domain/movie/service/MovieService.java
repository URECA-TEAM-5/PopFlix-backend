package com.popflix.domain.movie.service;

import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.movie.dto.GetDetailsResponseDto;
import com.popflix.domain.movie.dto.GetMovieListResponseDto;
import com.popflix.domain.movie.dto.GetMovieRatingResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieService {
    GetMovieRatingResponseDto getMovieRatings(Long movieId);

    Page<GetMovieListResponseDto> getMovieListByKeyword(String keyword, Pageable pageable,Long userId);

    Page<GetMovieListResponseDto> getMovieListByGenre(String genre, Pageable pageable, Long userId);

    Page<GetMovieListResponseDto> getAllMovies(Pageable pageable, Long userId);

    GetDetailsResponseDto getMovieDetails(Long movieId, Long userId);
}
