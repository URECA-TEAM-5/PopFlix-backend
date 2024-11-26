package com.popflix.domain.movie.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddRatingRequestDto {
    @Min(1)
    @Max(5)
    private Integer rating;

    @NotNull
    private Long userId;

    @NotNull
    private Long movieId;
}
