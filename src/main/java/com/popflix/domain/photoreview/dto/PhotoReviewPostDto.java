package com.popflix.domain.photoreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PhotoReviewPostDto {
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 500, message = "리뷰는 500자를 초과할 수 없습니다.")
    private String review;

    @NotNull(message = "이미지는 필수입니다.")
    private MultipartFile reviewImage;

    @NotNull(message = "영화 ID는 필수입니다.")
    private Long movieId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}