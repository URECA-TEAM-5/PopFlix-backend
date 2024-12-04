package com.popflix.auth.controller;

import com.popflix.auth.dto.OAuthProviderInfo;
import com.popflix.auth.dto.ProfileImage;
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
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;
    private final UserService userService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${app.auth.cookie.domain}")
    private String domain;

    @Value("${app.auth.cookie.secure}")
    private boolean secure;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

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
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = tokenProvider.resolveToken(request);
        if (accessToken != null) {
            tokenProvider.addToBlacklist(accessToken);
        }

        deleteTokenCookies(response);
        return ResponseEntity.ok(ApiUtil.success("로그아웃 되었습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiUtil.error(401, "Refresh token not found"));
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        String socialId = authentication.getName();

        if (!tokenProvider.validateRefreshToken(refreshToken, socialId)) {
            deleteTokenCookies(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiUtil.error(401, "Invalid refresh token"));
        }

        String newAccessToken = tokenProvider.createAccessToken(authentication);
        String newRefreshToken = tokenProvider.createRefreshToken(authentication);

        addTokenCookie(response, ACCESS_TOKEN_COOKIE_NAME, newAccessToken,
                (int) (tokenProvider.getAccessTokenValidityTime() / 1000));
        addTokenCookie(response, REFRESH_TOKEN_COOKIE_NAME, newRefreshToken,
                (int) (tokenProvider.getRefreshTokenValidityTime() / 1000));

        return ResponseEntity.ok(ApiUtil.success("토큰이 갱신되었습니다."));
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

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void deleteTokenCookies(HttpServletResponse response) {
        deleteCookie(response, ACCESS_TOKEN_COOKIE_NAME);
        deleteCookie(response, REFRESH_TOKEN_COOKIE_NAME);
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}