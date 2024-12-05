package com.popflix.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popflix.domain.personality.entity.Genre;
import com.popflix.domain.user.dto.UserInfoDto;
import com.popflix.domain.user.dto.UserPatchDto;
import com.popflix.domain.user.dto.UserRegistrationDto;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.Role;
import com.popflix.domain.user.service.UserService;
import com.popflix.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestPart("data") String dataString,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            UserRegistrationDto registrationDto = objectMapper.readValue(dataString, UserRegistrationDto.class);
            UserInfoDto userInfo = userService.completeRegistration(registrationDto, profileImage);
            return ResponseEntity.ok(ApiUtil.success(userInfo));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNicknameAvailability(@RequestParam String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(ApiUtil.success(isAvailable));
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getGenres() {
        List<Genre> genres = userService.getAllGenres();
        return ResponseEntity.ok(ApiUtil.success(genres));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            Authentication authentication,
            @RequestPart("data") String dataString,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        // 권한 검증
        if (!hasPermission(authentication, userId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        try {
            UserPatchDto patchRequest = objectMapper.readValue(dataString, UserPatchDto.class);
            UserInfoDto userInfo = userService.updateUser(userId, patchRequest, profileImage);
            return ResponseEntity.ok(ApiUtil.success(userInfo));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON format", e);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        if (!hasPermission(authentication, userId)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiUtil.success("회원 탈퇴가 완료되었습니다."));
    }

    private boolean hasPermission(Authentication authentication, Long userId) {
        User user = userService.getUserBySocialId(authentication.getName());
        return user.getUserId().equals(userId) ||
                user.getRole() == Role.ADMIN;
    }
}