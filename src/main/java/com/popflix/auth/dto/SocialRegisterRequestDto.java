package com.popflix.auth.dto;

import com.popflix.domain.user.enums.Gender;
import lombok.Data;

@Data
public class SocialRegisterRequestDto {
    private String email;
    private String socialId;
    private String name;
    private String social; // 제공자 이름 (Google, Naver 등)
    private String profileImage;
    private Gender gender; // 성별
}