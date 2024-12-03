package com.popflix.domain.user.service;

import com.popflix.domain.personality.entity.Genre;
import com.popflix.domain.user.dto.UserDto.UserInfo;
import com.popflix.domain.user.dto.UserDto.SignUpInfo;
import com.popflix.domain.user.dto.UserDto.GenreUpdateRequest;
import com.popflix.domain.user.dto.UserRegistrationDto;
import com.popflix.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User registerUser(SignUpInfo signUpInfo);
    UserInfo completeRegistration(UserRegistrationDto registrationDto, MultipartFile profileImage);
    void updateUserGenres(Long userId, GenreUpdateRequest request);
    boolean isNicknameAvailable(String nickname);
    List<Genre> getAllGenres();
    UserInfo getUserInfo(Long userId);
    User getUserByEmail(String email);
    User getUserBySocialId(String socialId);
}