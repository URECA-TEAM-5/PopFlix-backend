package com.popflix.auth.controller;

import com.popflix.auth.dto.OAuthProviderInfo;
import com.popflix.global.util.ApiUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthController_LoginTest {

    @Autowired
    private AuthController authController;

    @Test
    void testGetLoginUrls() {
        // When: /auth/login/urls 호출
        ResponseEntity<?> response = authController.getLoginUrls();

        // Then: 응답 상태와 데이터를 검증
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        ApiUtil.ApiSuccess<?> apiSuccess = (ApiUtil.ApiSuccess<?>) response.getBody();
        assertNotNull(apiSuccess);
        assertNotNull(apiSuccess.getResponse());
        assertTrue(apiSuccess.getResponse() instanceof Map);

        // 소셜 로그인 URL 검증
        Map<String, OAuthProviderInfo> providers = (Map<String, OAuthProviderInfo>) apiSuccess.getResponse();
        assertTrue(providers.containsKey("google"));
        assertTrue(providers.containsKey("naver"));

        // Google Provider 검증
        OAuthProviderInfo googleInfo = providers.get("google");
        assertEquals("/oauth2/authorization/google", googleInfo.getAuthUrl());
        assertEquals("/images/google-icon.png", googleInfo.getIcon());
        assertEquals("구글로 로그인", googleInfo.getName());

        // Naver Provider 검증
        OAuthProviderInfo naverInfo = providers.get("naver");
        assertEquals("/oauth2/authorization/naver", naverInfo.getAuthUrl());
        assertEquals("/images/naver-icon.png", naverInfo.getIcon());
        assertEquals("네이버로 로그인", naverInfo.getName());
    }
}