package com.popflix.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPostDto {
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 100, message = "리뷰는 100자를 초과할 수 없습니다.")
    private String review;

    @NotNull(message = "영화 ID는 필수입니다.")
    private Long movieId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}