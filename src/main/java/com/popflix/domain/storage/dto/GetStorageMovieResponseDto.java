package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Director;
import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.personality.entity.Genre;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetStorageMovieResponseDto {

    private Long id;
    private String title;
    private String posterPath;
    private List<String> genres;
    private String director;
    private LocalDate releaseDate;

    public GetStorageMovieResponseDto(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.posterPath = movie.getPosterPath();
        this.releaseDate = movie.getReleaseDate();

        // 장르 정보를 가져오기
        this.genres = movie.getMovieGenres().stream()
                .map(movieGenre -> movieGenre.getGenre().getName())
                .collect(Collectors.toList());

        // 감독 정보를 가져오기
        this.director = movie.getMovieDirectors().stream()
                .map(movieDirector -> movieDirector.getDirector().getName())
                .collect(Collectors.joining(", "));
    }
}
