package com.popflix.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String token, String email, long expirationTime) {
        redisTemplate.opsForValue().set(token, email, expirationTime, TimeUnit.MILLISECONDS);
    }

    public boolean validateRefreshToken(String token) {
        return redisTemplate.hasKey(token);
    }

    public String getEmailFromRefreshToken(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete(token);
    }
}