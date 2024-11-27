package com.popflix.domain.storage.service.impl;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.dto.GetStorageDetailResponse;
import com.popflix.domain.storage.dto.StorageResponseDto;
import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.storage.exception.DuplicateStorageNameException;
import com.popflix.domain.storage.exception.StorageNotFoundException;
import com.popflix.domain.storage.repository.MovieStorageRepository;
import com.popflix.domain.storage.repository.StorageRepository;
import com.popflix.domain.storage.service.StorageService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;
    private final MovieRepository movieRepository;
    private final MovieStorageRepository movieStorageRepository;
    private final UserRepository userRepository;

    // 보관함 생성
    @Transactional
    @Override
    public StorageResponseDto createStorage(CreateStorageRequestDto storageRequest) {
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

        Storage savedStorage = storageRepository.save(storage);

        return new StorageResponseDto(savedStorage);
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
    public List<StorageResponseDto> getStorageList() {
        List<Storage> storages = storageRepository.findAll();
        return storages.stream().map(StorageResponseDto::new).collect(Collectors.toList());
    }

    // 보관함 상세 조회
    @Override
    public GetStorageDetailResponse getStorageDetail(Long storageId) {
        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new EntityNotFoundException("보관함을 찾을 수 없습니다."));
        List<Movie> movies = movieStorageRepository.findMoviesByStorageId(storageId);
        return new GetStorageDetailResponse(storage, movies);
    }
}
