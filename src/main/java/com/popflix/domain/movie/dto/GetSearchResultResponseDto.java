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
public class GetSearchResultResponseDto {

    private Long id;
    private String title;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    private List<SimpleDto> genres;
    private List<SimpleDto> cast;
    private List<SimpleDto> directors;

    @JsonProperty("popcorn_average_score")
    private Double popcornAverageScore;

    @JsonProperty("liked_by_user")
    private Boolean likedByUser;

    @JsonProperty("like_count")
    private Long likeCount;

    @JsonProperty("poster_path")
    private String posterPath;

    @Builder
    public static GetSearchResultResponseDto from(
            Movie movie,
            Double averageRating,
            Boolean likedByUser,
            Long likeCount,
            String posterPath,
            List<SimpleDto> cast,
            List<SimpleDto> directors,
            List<SimpleDto> genres) {
        return GetSearchResultResponseDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .releaseDate(movie.getReleaseDate())
                .genres(genres)
                .cast(cast)
                .directors(directors)
                .popcornAverageScore(averageRating)
                .likedByUser(likedByUser)
                .likeCount(likeCount)
                .posterPath(posterPath)
                .build();
    }
}
