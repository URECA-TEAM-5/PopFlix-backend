package com.popflix.auth.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private final SecretKey key;
    private final long accessTokenValidityTime;
    private final long refreshTokenValidityTime;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String COOKIE_ACCESS_TOKEN_KEY = "access_token";
    private static final String REDIS_REFRESH_TOKEN_PREFIX = "RT:";
    private static final String REDIS_BLACKLIST_PREFIX = "BL:";

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidityTime,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidityTime,
            RedisTemplate<String, String> redisTemplate) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            keyBytes = digest.digest(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate secure key", e);
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityTime = accessTokenValidityTime;
        this.refreshTokenValidityTime = refreshTokenValidityTime;
        this.redisTemplate = redisTemplate;
    }

    public String createAccessToken(Authentication authentication) {
        String socialId = authentication.getName();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityTime);

        return Jwts.builder()
                .setSubject(socialId)
                .claim("auth", authorities)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        String socialId = authentication.getName();
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityTime);

        String refreshToken = Jwts.builder()
                .setSubject(socialId)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key)
                .compact();

        redisTemplate.opsForValue().set(
                REDIS_REFRESH_TOKEN_PREFIX + socialId,
                refreshToken,
                refreshTokenValidityTime,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public void addToBlacklist(String token) {
        Claims claims = parseToken(token);
        long remainingTime = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(
                    REDIS_BLACKLIST_PREFIX + token,
                    "blacklisted",
                    remainingTime,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (COOKIE_ACCESS_TOKEN_KEY.equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);

        Collection<? extends GrantedAuthority> authorities;
        String auth = claims.get("auth", String.class);

        if (auth != null) {
            authorities = Arrays.stream(auth.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                return false;
            }

            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateRefreshToken(String refreshToken, String socialId) {
        String storedToken = redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + socialId);
        if (!refreshToken.equals(storedToken)) {
            return false;
        }

        try {
            Claims claims = parseToken(refreshToken);
            return "refresh".equals(claims.get("type")) && !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_BLACKLIST_PREFIX + token));
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String extractSocialId(OAuth2User oauth2User, String registrationId) {
        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) oauth2User.getAttributes().get("response");
            return (String) response.get("id");
        }
        return oauth2User.getAttribute("sub");
    }

    public long getAccessTokenValidityTime() {
        return accessTokenValidityTime;
    }

    public long getRefreshTokenValidityTime() {
        return refreshTokenValidityTime;
    }
}