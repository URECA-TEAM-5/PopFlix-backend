package com.popflix.domain.photoreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoReviewPostDto {
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 1200, message = "리뷰는 1200자를 초과할 수 없습니다.")
    private String review;

    private MultipartFile reviewImage;

    @NotNull(message = "영화 ID는 필수입니다.")
    private Long movieId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    // reviewImage를 제외한 생성자
    public PhotoReviewPostDto(String review, Long movieId, Long userId) {
        this.review = review;
        this.movieId = movieId;
        this.userId = userId;
    }

    // reviewImage를 위한 setter
    public void setReviewImage(MultipartFile reviewImage) {
        this.reviewImage = reviewImage;
    }
}