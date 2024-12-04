package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetLikedStorageInfoResponseDto {
    private Long id;
    private String title;
    private String poster;
    private List<String> genres;

    public GetLikedStorageInfoResponseDto(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.poster = movie.getPosterPath();
        this.genres = movie.getMovieGenres().stream()
                .map(movieGenre -> movieGenre.getGenre().getName())
                .collect(Collectors.toList());
    }
}