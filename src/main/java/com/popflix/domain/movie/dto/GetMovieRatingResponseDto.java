package com.popflix.domain.movie.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetMovieRatingResponseDto {
    private Long movieId;
    private String title;
    private Double averageRating;
    private List<GetRatingResponseDto> ratings;


    @Builder
    public GetMovieRatingResponseDto(Long movieId, String title, Double averageRating, List<GetRatingResponseDto> ratings) {
        this.movieId = movieId;
        this.title = title;
        this.averageRating = averageRating;
        this.ratings = ratings;
    }
}
