package com.popflix.domain.storage.service;

import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.storage.dto.*;
import com.popflix.domain.storage.entity.Storage;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface StorageService {
    void createStorage(CreateStorageRequestDto storageRequest);

    void changeStatus(Long storageId, Long userId);

    List<GetStorageResponseDto> getStorageList(Long userId, String sort);

    GetStorageDetailResponseDto getStorageDetail(Long storageId, Long userId);

    List<GetStorageCreatorResponseDto> getOtherStoragesByCreator(Long storageId, Long userId);

    void addMovieToStorage(Long storageId, AddMovieRequestDto requestDto, Long userId);

    void removeMovieFromStorage(Long storageId, Long movieId, Long userId);

    void deleteStorage(Long storageId, Long userId);

    void updateStorageDetails(Long storageId, UpdateStorageRequestDto requestDto, Long userId);

    List<GetMyStorageResponseDto> getStoragesByCreator(Long userId);

    List<GetLikedStorageResponseDto> getLikedStorages(Long userId);
}
