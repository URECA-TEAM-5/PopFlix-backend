package com.popflix.domain.movie.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.popflix.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetDetailsResponseDto {

    private Long id;
    private String title;

    @JsonProperty("poster_path")
    private String posterPath;

    private String overview;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    private List<SimpleDto> cast;
    private List<SimpleDto> directors;

    @JsonProperty("genre_ids")
    private List<SimpleDto> genres;

    private Double popcornAverageScore;
    private Boolean likedByUser;

    private List<SimpleDto> reviewVideos;

    @Builder
    public static GetDetailsResponseDto from(
            Movie movie,
            Double averageRating,
            Boolean likedByUser,
            List<SimpleDto> cast,
            List<SimpleDto> directors,
            List<SimpleDto> genres,
            List<SimpleDto> reviewVideos) {
        return GetDetailsResponseDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .posterPath(movie.getPosterPath())
                .overview(movie.getOverview())
                .releaseDate(movie.getReleaseDate())
                .cast(cast)
                .directors(directors)
                .genres(genres)
                .popcornAverageScore(averageRating)
                .likedByUser(likedByUser)
                .reviewVideos(reviewVideos)
                .build();
    }
}
