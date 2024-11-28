package com.popflix.auth.service;

import com.popflix.auth.dto.SocialLoginRequestDto;
import com.popflix.auth.dto.SocialLoginResponseDto;
import com.popflix.auth.dto.SocialRegisterRequestDto;
import com.popflix.global.jwt.JwtUtil;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    // 회원가입 처리
    public void registerUser(SocialRegisterRequestDto requestDto) {
        User user = User.builder()
                .email(requestDto.getEmail())
                .socialId(requestDto.getSocialId())
                .name(requestDto.getName())
                .authType(AuthType.valueOf(requestDto.getSocial()))
                .profileImage(requestDto.getProfileImage())
                .gender(requestDto.getGender())
                .build();
        userRepository.save(user);
    }

    // 로그인 처리
    public SocialLoginResponseDto loginUser(SocialLoginRequestDto requestDto) {
        String email = jwtUtil.getEmailFromToken(requestDto.getSocialToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 사용자입니다."));

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        return new SocialLoginResponseDto(accessToken, refreshToken);
    }

    // 로그아웃 처리
    public void logoutUser(String accessToken) {
        String token = accessToken.replace("Bearer ", "");
        String email = jwtUtil.getEmailFromToken(token);

        redisTemplate.delete(email); // 로그아웃 시 Redis에서 해당 유저의 정보 삭제
    }
}