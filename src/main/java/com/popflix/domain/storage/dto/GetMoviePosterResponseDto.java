package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetMoviePosterResponseDto {
    private Long id;
    private String poster;

    public GetMoviePosterResponseDto(Movie movie) {
        this.id = movie.getId();
        this.poster = movie.getPosterPath();
    }
}

