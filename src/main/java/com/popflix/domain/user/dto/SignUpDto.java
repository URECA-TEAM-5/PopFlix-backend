package com.popflix.domain.user.dto;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private AuthType authType;
    private String socialId;
    private List<Long> genreIds;

    public User toEntity() {
        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .profileImage(profileImage)
                .authType(authType)
                .socialId(socialId)
                .role(Role.USER)
                .build();
    }
}