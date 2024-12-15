package com.popflix.auth.controller;

import com.popflix.auth.util.CookieUtil;
import com.popflix.auth.token.TokenProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthController_LogoutTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CookieUtil cookieUtil;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    void logout_success() throws Exception {
        // Mocking CookieUtil methods
        Mockito.doNothing().when(cookieUtil).deleteAccessTokenCookie(Mockito.any());
        Mockito.doNothing().when(cookieUtil).deleteRefreshTokenCookie(Mockito.any());

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk());
    }
}
