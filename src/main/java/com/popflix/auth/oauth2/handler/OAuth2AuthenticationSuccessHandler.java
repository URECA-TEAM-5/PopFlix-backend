package com.popflix.auth.oauth2.handler;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.exception.UserNotFoundException;
import com.popflix.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import com.popflix.auth.token.TokenProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Value("${app.auth.cookie.domain}")
    private String domain;

    @Value("${app.auth.cookie.secure}")
    private boolean secure;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String id;
        if ("naver".equals(registrationId)) {
            Map<String, Object> response_data = (Map<String, Object>) oauth2User.getAttributes().get("response");
            id = (String) response_data.get("id");
        } else {
            id = oauth2User.getAttribute("sub");
        }

        String socialId = registrationId + "_" + id;

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(socialId, null, authorities);

        String accessToken = tokenProvider.createAccessToken(newAuth);
        log.info("Generated Access Token: Bearer {}", accessToken);
        String refreshToken = tokenProvider.createRefreshToken(newAuth);

        addTokenCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken,
                (int) (tokenProvider.getAccessTokenValidityTime() / 1000));
        addTokenCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken,
                (int) (tokenProvider.getRefreshTokenValidityTime() / 1000));

        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(UserNotFoundException::new);

        String targetUrl = user.getNickname() == null ?
                "/auth/register" : "/";

        String redirectUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("isNewUser", user.getNickname() == null)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(secure);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}