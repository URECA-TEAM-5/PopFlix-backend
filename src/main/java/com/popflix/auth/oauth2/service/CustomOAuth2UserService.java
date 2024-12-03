package com.popflix.auth.oauth2.service;

import com.popflix.auth.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.popflix.auth.oauth2.userinfo.NaverOAuth2UserInfo;
import com.popflix.auth.oauth2.userinfo.OAuth2UserInfo;
import com.popflix.domain.user.dto.SignUpDto;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        String socialId = registrationId + "_" + userInfo.getId();
        User user = processUser(socialId, userInfo, registrationId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                oauth2User.getAttributes(),
                userRequest.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthType.GOOGLE.name())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthType.NAVER.name())) {
            return new NaverOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
        }
    }

    private String generateUniqueNickname(String baseName) {
        String nickname = baseName;
        int suffix = 1;
        while (userRepository.existsByNickname(nickname)) {
            nickname = baseName + suffix++;
        }
        return nickname;
    }

    private User processUser(String socialId, OAuth2UserInfo userInfo, String registrationId) {
        log.info("Processing OAuth2 login - Provider: {}, Email: {}, SocialId: {}",
                registrationId, userInfo.getEmail(), socialId);

        try {
            return userService.getUserBySocialId(socialId);
        } catch (RuntimeException e) {
            log.info("Creating new user for Provider: {}", registrationId);
            return userService.registerUser(
                    SignUpDto.builder()
                            .email(userInfo.getEmail())
                            .name(userInfo.getName())
                            .nickname(generateUniqueNickname(userInfo.getName()))
                            .profileImage(userInfo.getImageUrl())
                            .authType(AuthType.valueOf(registrationId.toUpperCase()))
                            .socialId(socialId)
                            .build()
            );
        }
    }
}