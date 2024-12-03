package com.popflix.auth.oauth2.handler;

import com.popflix.auth.token.TokenProvider;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.exception.UserNotFoundException;
import com.popflix.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed");
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        log.info("Access Token: Bearer {}", accessToken);
        log.info("Refresh Token: Bearer {}", refreshToken);

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String id;
        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) oauth2User.getAttributes().get("response");
            id = (String) response.get("id");
        } else {
            id = oauth2User.getAttribute("sub");
        }
        String socialId = registrationId + "_" + id;

        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(UserNotFoundException::new);

        redisTemplate.opsForValue()
                .set("RT:" + socialId, refreshToken, 30, TimeUnit.DAYS);

        String targetUrl = user.getNickname() == null ?
                "/auth/register" : "/";

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", "Bearer " + accessToken)
                .queryParam("refreshToken", "Bearer " + refreshToken)
                .queryParam("isNewUser", user.getNickname() == null)
                .build().toUriString();
    }
}