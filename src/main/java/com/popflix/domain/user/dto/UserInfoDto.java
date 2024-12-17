package com.popflix.domain.user.dto;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private Long userId;
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private AuthType authType;
    private String socialId;
    private Gender gender;
    private List<Long> genreIds;

    public static UserInfoDto from(User user) {
        return UserInfoDto.builder()
                .userId(user.getUserId())
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