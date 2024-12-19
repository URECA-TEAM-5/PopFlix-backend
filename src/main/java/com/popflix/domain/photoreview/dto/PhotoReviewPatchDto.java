package com.popflix.domain.photoreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoReviewPatchDto {
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 1200, message = "리뷰는 1200자를 초과할 수 없습니다.")
    private String review;

    private MultipartFile reviewImage;
}