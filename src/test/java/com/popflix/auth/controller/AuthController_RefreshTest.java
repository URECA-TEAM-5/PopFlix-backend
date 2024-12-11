package com.popflix.auth.controller;

import com.popflix.auth.token.TokenProvider;
import com.popflix.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthController_RefreshTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private UserService userService;

    @MockBean
    private RedisTemplate<String, String> redisTemplate; // RedisTemplate 모킹 추가

    private String mockRefreshToken;
    private String mockNewAccessToken;
    private String mockNewRefreshToken;
    private Authentication mockAuthentication;

    @BeforeEach
    void setup() {
        // 테스트용 Mock 데이터 초기화
        mockRefreshToken = "mockRefreshToken";
        mockNewAccessToken = "mockNewAccessToken";
        mockNewRefreshToken = "mockNewRefreshToken";

        // Authentication 객체 모킹
        mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(mockAuthentication.getName()).thenReturn("mockSocialId");

        // RedisTemplate 동작 모킹
        Mockito.when(redisTemplate.opsForValue().get(anyString()))
                .thenReturn(mockRefreshToken);

        Mockito.doNothing().when(redisTemplate.opsForValue())
                .set(anyString(), anyString(), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void refresh_success() throws Exception {
        // TokenProvider 메서드 모킹
        Mockito.when(tokenProvider.resolveToken(any()))
                .thenReturn(mockRefreshToken);

        Mockito.when(tokenProvider.getAuthentication(eq(mockRefreshToken)))
                .thenReturn(mockAuthentication);

        Mockito.when(tokenProvider.validateRefreshToken(eq(mockRefreshToken), eq("mockSocialId")))
                .thenReturn(true);

        Mockito.when(tokenProvider.createAccessToken(any(Authentication.class)))
                .thenReturn(mockNewAccessToken);

        Mockito.when(tokenProvider.createRefreshToken(any(Authentication.class)))
                .thenReturn(mockNewRefreshToken);

        // 테스트 요청 수행
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refresh_token", mockRefreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("토큰이 갱신되었습니다."));
    }

    @Test
    void refresh_invalidToken() throws Exception {
        // TokenProvider 메서드 모킹
        Mockito.when(tokenProvider.resolveToken(any()))
                .thenReturn("invalidRefreshToken");

        Mockito.when(tokenProvider.getAuthentication(eq("invalidRefreshToken")))
                .thenReturn(null);

        // 테스트 요청 수행
        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refresh_token", "invalidRefreshToken"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid refresh token"));
    }
}