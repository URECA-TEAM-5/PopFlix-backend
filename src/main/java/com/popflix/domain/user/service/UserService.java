package com.popflix.domain.user.service;

import com.popflix.domain.personality.entity.Genre;
import com.popflix.domain.user.dto.*;
import com.popflix.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    User registerUser(SignUpDto signUpDto);
    UserInfoDto completeRegistration(UserRegistrationDto registrationDto, MultipartFile profileImage, String socialId);
    void updateUserGenres(Long userId, GenrePatchDto request);
    boolean isNicknameAvailable(String nickname);
    List<Genre> getAllGenres();
    UserInfoDto getUserInfo(Long userId);
    User getUserByEmail(String email);
    User getUserBySocialId(String socialId);
    UserInfoDto updateUser(Long userId, UserPatchDto patchRequest, MultipartFile profileImage);
    void deleteUser(Long userId);
}