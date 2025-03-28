package com.popflix.auth.controller;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.enums.Role;
import com.popflix.domain.user.exception.UserNotFoundException;
import com.popflix.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthController_GetCurrentUserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication mockAuthentication;

    @Test
    void getCurrentUser_Unauthenticated() throws Exception {
        // 인증되지 않은 상태
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("인증되지 않은 사용자입니다."));
    }

    @Test
    void getCurrentUser_ValidUser() throws Exception {
        // Mock Authentication 객체
        Mockito.when(mockAuthentication.getName()).thenReturn("mockSocialId");

        // Mock User 객체 생성
        User mockUser = User.builder()
                .email("test@example.com")
                .name("Mock Name")
                .nickname("mockNickname")
                .profileImage("http://example.com/profile.jpg")
                .authType(AuthType.GOOGLE)
                .socialId("mockSocialId")
                .role(Role.USER)
                .build();

        // UserService 동작 모킹
        Mockito.when(userService.getUserBySocialId("mockSocialId"))
                .thenReturn(mockUser);

        // 요청 수행
        mockMvc.perform(get("/auth/me")
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.socialId").value("mockSocialId"))
                .andExpect(jsonPath("$.response.email").value("test@example.com"))
                .andExpect(jsonPath("$.response.nickname").value("mockNickname"))
                .andExpect(jsonPath("$.response.authType").value("GOOGLE"));
    }

    @Test
    void getCurrentUser_UserNotFound() throws Exception {
        // Mock Authentication 객체
        Mockito.when(mockAuthentication.getName()).thenReturn("nonexistentSocialId");

        // UserService에서 예외 발생 모킹
        Mockito.when(userService.getUserBySocialId("nonexistentSocialId"))
                .thenThrow(new UserNotFoundException());

        // 요청 수행
        mockMvc.perform(get("/auth/me")
                        .principal(mockAuthentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }
}