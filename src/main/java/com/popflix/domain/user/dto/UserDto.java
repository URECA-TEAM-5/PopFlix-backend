package com.popflix.domain.user.dto;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.enums.Gender;
import com.popflix.domain.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String email;
        private String name;
        private String nickname;
        private String profileImage;
        private AuthType authType;
        private String socialId;
        private Gender gender;
        private List<Long> genreIds;

        public static UserInfo from(User user) {
            return UserInfo.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .authType(user.getAuthType())
                    .socialId(user.getSocialId())
                    .gender(user.getGender())
                    .genreIds(user.getUserGenres().stream()
                            .map(userGenre -> userGenre.getGenreId())
                            .toList())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpInfo {
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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenreUpdateRequest {
        private List<Long> genreIds;
    }
}