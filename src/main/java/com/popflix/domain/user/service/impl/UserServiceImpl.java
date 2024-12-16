package com.popflix.domain.user.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.popflix.domain.personality.entity.Genre;
import com.popflix.domain.user.dto.*;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.entity.UserGenre;
import com.popflix.domain.user.exception.*;
import com.popflix.domain.user.repository.UserGenreRepository;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.domain.personality.repository.GenreRepository;
import com.popflix.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
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
    public User registerUser(SignUpDto signUpDto) {
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new DuplicateEmailException();
        }
        if (userRepository.existsByNickname(signUpDto.getNickname())) {
            throw new DuplicateNicknameException();
        }

        User user = userRepository.save(signUpDto.toEntity());

        if (signUpDto.getGenreIds() != null) {
            signUpDto.getGenreIds().forEach(genreId -> {
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
    public UserInfoDto completeRegistration(UserRegistrationDto registrationDto, MultipartFile profileImage, String socialId) {
        Genre genre = genreRepository.findById(registrationDto.getGenreId())
                .orElseThrow(InvalidGenreException::new);

        log.info("Attempting to find user with socialId: {}", socialId);  // 로그 추가

        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(UserNotFoundException::new);

        log.info("Found user: {}", user.getSocialId());  // 로그 추가

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
        userGenreRepository.save(userGenre);
        user.addUserGenre(userGenre);

        return UserInfoDto.from(user);
    }

    @Override
    @Transactional
    public void updateUserGenres(Long userId, GenrePatchDto patchRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userGenreRepository.deleteAllByUserId(userId);

        patchRequest.getGenreIds().forEach(genreId -> {
            UserGenre userGenre = UserGenre.builder()
                    .user(user)
                    .genreId(genreId)
                    .build();
            userGenreRepository.save(userGenre);
        });
    }

    @Override
    @Transactional
    public UserInfoDto updateUser(Long userId, UserPatchDto patchRequest, MultipartFile profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getNickname().equals(patchRequest.getNickname()) &&
                userRepository.existsByNickname(patchRequest.getNickname())) {
            throw new DuplicateNicknameException();
        }

        Genre genre = genreRepository.findById(patchRequest.getGenreId())
                .orElseThrow(InvalidGenreException::new);

        String profileImageUrl = user.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            if (profileImageUrl != null && !profileImageUrl.contains("/defaults/")) {
                try {
                    amazonS3.deleteObject(bucket, extractFileNameFromUrl(profileImageUrl));
                } catch (Exception e) {
                    log.warn("Failed to delete old profile image", e);
                }
            }
            profileImageUrl = uploadProfileImage(profileImage, user.getUserId());
        } else if (patchRequest.getDefaultProfileImage() != null) {
            profileImageUrl = patchRequest.getDefaultProfileImage();
        }

        user.updateProfile(patchRequest.getNickname(), profileImageUrl);
        user.setGender(patchRequest.getGender());

        userGenreRepository.deleteAllByUserId(user.getUserId());
        UserGenre userGenre = UserGenre.builder()
                .genreId(genre.getId())
                .user(user)
                .build();
        userGenreRepository.save(userGenre);
        user.addUserGenre(userGenre);

        return UserInfoDto.from(user);
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
    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return UserInfoDto.from(user);
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

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userGenreRepository.deleteAllByUserId(userId);

        String profileImageUrl = user.getProfileImage();
        if (profileImageUrl != null && !profileImageUrl.contains("/defaults/")) {
            try {
                amazonS3.deleteObject(bucket, extractFileNameFromUrl(profileImageUrl));
            } catch (Exception e) {
                log.warn("Failed to delete profile image during user deletion", e);
            }
        }

        userRepository.delete(user);
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

    private String extractFileNameFromUrl(String imageUrl) {
        String bucketPrefix = bucket + ".s3.amazonaws.com/";
        if (imageUrl.contains(bucketPrefix)) {
            return imageUrl.substring(imageUrl.indexOf(bucketPrefix) + bucketPrefix.length());
        }
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}