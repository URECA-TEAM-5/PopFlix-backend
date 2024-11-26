package com.popflix.domain.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetTMDBDetailsResponseDto {

    private List<Movie> results; // TMDb API에서 받은 인기 영화 목록

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Movie {
        private Long id;

        private String title;

        @JsonProperty("poster_path")
        private String posterPath;

        private String overview;

        @JsonProperty("release_date")
        private LocalDate releaseDate;

        @JsonProperty("genre_ids")
        private List<Integer> genreIds;
    }
}
