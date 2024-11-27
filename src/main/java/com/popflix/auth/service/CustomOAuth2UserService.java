package com.popflix.auth.service;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.domain.user.enums.AuthType;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String platform = userRequest.getClientRegistration().getRegistrationId();
        AuthType authType = AuthType.valueOf(platform.toUpperCase());

        String email = oAuth2User.getAttribute("email");

        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .authType(authType)
                    .build();
            return userRepository.save(newUser);
        });

        return oAuth2User;
    }
}