package com.popflix.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popflix.domain.personality.entity.Genre;
import com.popflix.domain.user.dto.UserDto;
import com.popflix.domain.user.dto.UserRegistrationDto;
import com.popflix.domain.user.service.UserService;
import com.popflix.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRegistrationController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestPart("data") String dataString,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserRegistrationDto registrationDto = mapper.readValue(dataString, UserRegistrationDto.class);
            UserDto.UserInfo userInfo = userService.completeRegistration(registrationDto, profileImage);
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
}