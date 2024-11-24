package com.popflix.domain.movie.dto;

import com.popflix.domain.movie.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetMovieListResponseDto {
    private Long movieId;
    private String title;
    private String posterUrl;
    private double popcornScore;
    private Long likeCount;

    public static GetMovieListResponseDto from(Movie movie, Double averageRating) {
        return GetMovieListResponseDto.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .posterUrl(movie.getPosterPath())
                .popcornScore(averageRating)
                .likeCount(movie.getLikeCount())
                .build();
    }
}
