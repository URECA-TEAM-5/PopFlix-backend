package com.popflix.auth.controller;

import com.popflix.auth.dto.OAuthProviderInfo;
import com.popflix.auth.dto.ProfileImage;
import com.popflix.auth.dto.TokenDto;
import com.popflix.auth.service.AuthService;
import com.popflix.auth.token.TokenProvider;
import com.popflix.domain.user.dto.UserInfoDto;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.service.UserService;
import com.popflix.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenProvider tokenProvider;
    private final UserService userService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @GetMapping("/login/urls")
    public ResponseEntity<?> getLoginUrls() {
        Map<String, OAuthProviderInfo> providers = new HashMap<>();

        providers.put("google", OAuthProviderInfo.builder()
                .authUrl("/oauth2/authorization/google")
                .icon("/images/google-icon.png")
                .name("구글로 로그인")
                .build());

        providers.put("naver", OAuthProviderInfo.builder()
                .authUrl("/oauth2/authorization/naver")
                .icon("/images/naver-icon.png")
                .name("네이버로 로그인")
                .build());

        return ResponseEntity.ok(ApiUtil.success(providers));
    }

    @GetMapping("/default-profile-images")
    public ResponseEntity<?> getDefaultProfileImages() {
        List<ProfileImage> defaultImages = Arrays.asList(
                new ProfileImage("profile1", String.format("https://%s.s3.amazonaws.com/defaults/profile1.png", bucket)),
                new ProfileImage("profile2", String.format("https://%s.s3.amazonaws.com/defaults/profile2.png", bucket)),
                new ProfileImage("profile3", String.format("https://%s.s3.amazonaws.com/defaults/profile3.png", bucket)),
                new ProfileImage("profile4", String.format("https://%s.s3.amazonaws.com/defaults/profile4.png", bucket)),
                new ProfileImage("profile5", String.format("https://%s.s3.amazonaws.com/defaults/profile5.png", bucket)),
                new ProfileImage("profile6", String.format("https://%s.s3.amazonaws.com/defaults/profile6.png", bucket))
        );

        return ResponseEntity.ok(ApiUtil.success(defaultImages));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken) {
        String accessToken = bearerToken.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authService.logout(accessToken, authentication.getName());
        return ResponseEntity.ok(ApiUtil.success("로그아웃 되었습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenDto.Request request) {
        String refreshToken = request.getRefreshToken().replace("Bearer ", "");

        if (!tokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.badRequest()
                    .body(ApiUtil.error(400, "Invalid refresh token"));
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(authentication);

        authService.refreshToken(refreshToken, newRefreshToken, authentication.getName());

        TokenDto.Response response = TokenDto.Response.builder()
                .accessToken("Bearer " + newAccessToken)
                .refreshToken("Bearer " + newRefreshToken)
                .build();

        return ResponseEntity.ok(ApiUtil.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiUtil.error(HttpStatus.UNAUTHORIZED.value(), "인증되지 않은 사용자입니다."));
        }

        String socialId = authentication.getName();
        User user = userService.getUserBySocialId(socialId);
        UserInfoDto userInfo = UserInfoDto.from(user);
        return ResponseEntity.ok(ApiUtil.success(userInfo));
    }
}