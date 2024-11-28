package com.popflix.auth.service;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // OAuth2UserRequest를 이용하여 사용자 정보를 로드하고, 필요한 데이터를 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // DefaultOAuth2UserService의 loadUser 메서드를 호출하여 OAuth2User 정보 로드
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // OAuth2UserRequest에서 클라이언트 등록 정보를 통해 소셜 플랫폼을 식별
        String platform = userRequest.getClientRegistration().getRegistrationId();

        // 플랫폼 이름을 기반으로 AuthType을 결정 (예: GOOGLE, NAVER 등)
        AuthType authType = AuthType.valueOf(platform.toUpperCase());

        // OAuth2User로부터 email을 추출
        String email = oAuth2User.getAttribute("email");

        // 이메일로 사용자 정보 조회, 없으면 새로 생성하여 저장
        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email) // 이메일 설정
                    .authType(authType) // 로그인 타입 설정 (구글, 네이버 등)
                    .build();
            return userRepository.save(newUser); // 새 사용자 저장
        });

        // OAuth2User 정보를 반환
        return oAuth2User;
    }
}