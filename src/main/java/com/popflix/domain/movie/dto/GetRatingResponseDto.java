package com.popflix.domain.movie.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetRatingResponseDto {
    private Long userId;
    private Integer rating;


    @Builder
    public GetRatingResponseDto(Long userId, Integer rating) {
        this.userId = userId;
        this.rating = rating;
    }
}
