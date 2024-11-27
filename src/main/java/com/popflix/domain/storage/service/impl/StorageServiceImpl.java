package com.popflix.domain.storage.service.impl;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.dto.GetStorageDetailResponseDto;
import com.popflix.domain.storage.dto.GetStorageResponseDto;
import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.storage.exception.DuplicateStorageNameException;
import com.popflix.domain.storage.exception.StorageNotFoundException;
import com.popflix.domain.storage.repository.MovieStorageRepository;
import com.popflix.domain.storage.repository.StorageLikeRepository;
import com.popflix.domain.storage.repository.StorageRepository;
import com.popflix.domain.storage.service.StorageService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final StorageLikeRepository storageLikeRepository;
    private final MovieStorageRepository movieStorageRepository;
    private final UserRepository userRepository;

    // 보관함 생성
    @Transactional
    @Override
    public void createStorage(CreateStorageRequestDto storageRequest) {
        User user = userRepository.findById(storageRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(storageRequest.getUserId()));

        if (storageRepository.existsByUserAndStorageName(user, storageRequest.getStorageName())) {
            throw new DuplicateStorageNameException(storageRequest.getStorageName());
        }

        Storage storage = Storage.builder()
                .storageName(storageRequest.getStorageName())
                .storageOverview(storageRequest.getStorageOverview())
                .storageImage(storageRequest.getStorageImage())
                .user(user)
                .isPublic(false)
                .likeCount(0L)
                .movieCount(0L)
                .build();

        storageRepository.save(storage);
    }

    // 보관함 공개 여부 토글 (공개 ↔ 비공개)
    @Transactional
    @Override
    public void changeStatus(Long storageId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        storage.changeStatus();

        storageRepository.save(storage);
    }

    // 보관함 목록 조회
    @Override
    public List<GetStorageResponseDto> getStorageList(Long userId) {
        List<Storage> storages = storageRepository.findAll();

        return storages.stream()
                .map(storage -> GetStorageResponseDto.builder()
                        .id(storage.getId())
                        .storageName(storage.getStorageName())
                        .username(storage.getUser().getName())
                        .movieCount(storage.getMovieCount())
                        .likeCount(storage.getLikeCount())
                        .isLiked(storageLikeRepository.existsByStorage_IdAndUser_UserIdAndIsLiked(storage.getId(), userId, true))
                        .storageImage(storage.getStorageImage())
                        .build())
                .collect(Collectors.toList());
    }

    // 보관함 상세 조회
    @Override
    public GetStorageDetailResponseDto getStorageDetail(Long storageId, Long userId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        List<Movie> movies = storage.getMovies();
        boolean isLiked = storageLikeRepository.existsByStorage_IdAndUser_UserIdAndIsLiked(storageId, userId, true);

        return new GetStorageDetailResponseDto(storage, movies, userId, isLiked);
    }
}
