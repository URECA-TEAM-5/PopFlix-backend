package com.popflix.auth.token;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TokenProviderTest {

    private TokenProvider tokenProvider;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // RedisTemplate Mock 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // TokenProvider 직접 인스턴스화
        tokenProvider = new TokenProvider(
                "your_very_long_and_secure_secret_key_that_is_at_least_512_bits_long",
                1800000, // Access Token 유효 시간 (ms)
                604800000, // Refresh Token 유효 시간 (ms)
                redisTemplate
        );
    }

    @Test
    void addToBlacklist() {
        // Given: 테스트용 Access Token 생성
        String accessToken = tokenProvider.createAccessToken(
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );

        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 60000));

        // Redis에 블랙리스트 추가 확인
        tokenProvider.addToBlacklist(accessToken);

        // Then: Redis에 값이 저장되었는지 확인
        verify(valueOperations, times(1)).set(
                eq("BL:" + accessToken),
                eq("blacklisted"),
                anyLong(),
                eq(TimeUnit.MILLISECONDS)
        );
    }
}