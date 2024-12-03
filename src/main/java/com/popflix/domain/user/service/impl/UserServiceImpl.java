package com.popflix.domain.user.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.popflix.domain.personality.entity.Genre;
import com.popflix.domain.user.dto.UserDto.UserInfo;
import com.popflix.domain.user.dto.UserDto.SignUpInfo;
import com.popflix.domain.user.dto.UserDto.GenreUpdateRequest;
import com.popflix.domain.user.dto.UserRegistrationDto;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.entity.UserGenre;
import com.popflix.domain.user.exception.*;
import com.popflix.domain.user.repository.UserGenreRepository;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.domain.personality.repository.GenreRepository;
import com.popflix.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserGenreRepository userGenreRepository;
    private final GenreRepository genreRepository;
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    @Transactional
    public User registerUser(SignUpInfo signUpInfo) {
        if (userRepository.existsByEmail(signUpInfo.getEmail())) {
            throw new DuplicateEmailException();
        }
        if (userRepository.existsByNickname(signUpInfo.getNickname())) {
            throw new DuplicateNicknameException();
        }

        User user = userRepository.save(signUpInfo.toEntity());

        if (signUpInfo.getGenreIds() != null) {
            signUpInfo.getGenreIds().forEach(genreId -> {
                UserGenre userGenre = UserGenre.builder()
                        .genreId(genreId)
                        .user(user)
                        .build();
                user.addUserGenre(userGenre);
            });
        }

        return user;
    }

    @Override
    @Transactional
    public UserInfo completeRegistration(UserRegistrationDto registrationDto, MultipartFile profileImage) {
        Genre genre = genreRepository.findById(registrationDto.getGenreId())
                .orElseThrow(() -> new InvalidGenreException());

        User user = userRepository.findBySocialId(SecurityContextHolder.getContext()
                        .getAuthentication().getName())
                .orElseThrow(UserNotFoundException::new);

        if (userRepository.existsByNickname(registrationDto.getNickname())) {
            throw new DuplicateNicknameException();
        }

        String profileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileImageUrl = uploadProfileImage(profileImage, user.getUserId());
        } else if (registrationDto.getDefaultProfileImage() != null) {
            profileImageUrl = registrationDto.getDefaultProfileImage();
        }

        user.updateProfile(registrationDto.getNickname(), profileImageUrl);
        user.setGender(registrationDto.getGender());

        UserGenre userGenre = UserGenre.builder()
                .genreId(genre.getId())
                .user(user)
                .build();
        user.addUserGenre(userGenre);

        return UserInfo.from(user);
    }

    @Override
    @Transactional
    public void updateUserGenres(Long userId, GenreUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userGenreRepository.deleteAllByUserId(userId);

        request.getGenreIds().forEach(genreId -> {
            UserGenre userGenre = UserGenre.builder()
                    .user(user)
                    .genreId(genreId)
                    .build();
            userGenreRepository.save(userGenre);
        });
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public UserInfo getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return UserInfo.from(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User getUserBySocialId(String socialId) {
        return userRepository.findBySocialId(socialId)
                .orElseThrow(UserNotFoundException::new);
    }

    private String uploadProfileImage(MultipartFile file, Long userId) {
        try {
            String fileName = "profile/" + userId + "/" +
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(
                    bucket, fileName, file.getInputStream(), metadata
            ));

            return amazonS3.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
        }
    }
}