package com.popflix.domain.user.dto;

import com.popflix.domain.user.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 1, max = 10, message = "닉네임은 1자 이상 10자 이하여야 합니다.")
    @Pattern(regexp = "^[^\\s]+$", message = "닉네임에 공백을 포함할 수 없습니다.")
    private String nickname;

    @NotNull(message = "장르 선택은 필수입니다.")
    private Long genreId;

    @NotNull(message = "성별 선택은 필수입니다.")
    private Gender gender;

    private String defaultProfileImage;
}