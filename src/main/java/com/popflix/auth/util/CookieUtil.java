package com.popflix.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${app.auth.cookie.domain}")
    private String domain;

    @Value("${app.auth.cookie.secure}")
    private boolean secure;

    @Value("${app.auth.cookie.access-token-name}")
    private String accessTokenName;

    @Value("${app.auth.cookie.refresh-token-name}")
    private String refreshTokenName;

    @Value("${app.auth.cookie.access-token-expiry}")
    private int accessTokenExpiry;

    @Value("${app.auth.cookie.refresh-token-expiry}")
    private int refreshTokenExpiry;

    public void addAccessTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(accessTokenName, token)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("None")
                .maxAge(accessTokenExpiry)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenName, token)
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("None")
                .maxAge(refreshTokenExpiry)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(accessTokenName, "")
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("None")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenName, "")
                .domain(domain)
                .path("/")
                .httpOnly(true)
                .secure(secure)
                .sameSite("None")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (accessTokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (refreshTokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}