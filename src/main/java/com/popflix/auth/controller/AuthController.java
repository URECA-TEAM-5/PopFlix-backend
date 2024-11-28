package com.popflix.auth.controller;

import com.popflix.auth.dto.AuthResponse;
import com.popflix.auth.service.RefreshTokenService;
import com.popflix.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/oauth2/success")
    public ResponseEntity<AuthResponse> oauth2Success() {
        String email = "user@example.com"; // OAuth2 로그인 성공 후 이메일 가져오기
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        refreshTokenService.saveRefreshToken(refreshToken, email, 604800000); // 7일 유효

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @GetMapping("/oauth2/failure")
    public ResponseEntity<String> oauth2Failure() {
        return ResponseEntity.badRequest().body("OAuth2 Login Failed");
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        String email = refreshTokenService.getEmailFromRefreshToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(email);

        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Refresh-Token") String refreshToken) {
        if (refreshTokenService.validateRefreshToken(refreshToken)) {
            refreshTokenService.deleteRefreshToken(refreshToken);
        }
        return ResponseEntity.ok("Logged out successfully");
    }
}