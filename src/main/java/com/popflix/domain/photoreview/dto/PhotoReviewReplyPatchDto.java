package com.popflix.domain.photoreview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoReviewReplyPatchDto {
    @NotBlank(message = "대댓글 내용은 필수입니다.")
    @Size(max = 100, message = "대댓글은 100자를 초과할 수 없습니다.")
    private String reply;
}
