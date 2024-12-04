package com.popflix.auth.service;

import com.popflix.auth.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenProvider tokenProvider;

    public void logout(String accessToken, String socialId) {
        String token = accessToken.startsWith("Bearer ")
                ? accessToken.substring(7).trim()
                : accessToken.trim();

        log.info("Logging out user: {}", socialId);
        log.info("Access token to blacklist: {}", token);

        String blacklistKey = "BL:" + token;
        redisTemplate.opsForValue().set(blacklistKey, "logout", 1, TimeUnit.HOURS);

        String value = redisTemplate.opsForValue().get(blacklistKey);
        log.info("Stored value in Redis for key {}: {}", blacklistKey, value);

        String refreshTokenKey = "RT:" + socialId;
        Boolean deleted = redisTemplate.delete(refreshTokenKey);
        log.info("Refresh token deleted for key {}: {}", refreshTokenKey, deleted);
    }

    public void refreshToken(String oldRefreshToken, String newRefreshToken, String socialId) {
        redisTemplate.delete("RT:" + socialId);
        redisTemplate.opsForValue()
                .set("RT:" + socialId, newRefreshToken,
                        tokenProvider.getRefreshTokenValidityTime(), TimeUnit.MILLISECONDS);
    }
}