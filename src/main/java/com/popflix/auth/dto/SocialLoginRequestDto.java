package com.popflix.auth.dto;

import lombok.Data;

@Data
public class SocialLoginRequestDto {
    private String socialToken; // 소셜 인증 토큰
    private String provider;    // 제공자 (Google, Naver 등)
}