package com.popflix.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetRecommendedMovieResponseDto {
    private Long id;
    private String posterPath;
}
