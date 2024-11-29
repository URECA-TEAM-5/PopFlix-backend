package com.popflix.domain.storage.service;

import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.dto.GetStorageCreatorResponseDto;
import com.popflix.domain.storage.dto.GetStorageDetailResponseDto;
import com.popflix.domain.storage.dto.GetStorageResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface StorageService {
    void createStorage(CreateStorageRequestDto storageRequest);

    void changeStatus(Long storageId);

    List<GetStorageResponseDto> getStorageList(Long userId, String sort);

    GetStorageDetailResponseDto getStorageDetail(Long storageId, Long userId);

    List<GetStorageCreatorResponseDto> getOtherStoragesByCreator(Long storageId, Long userId);

    void addMovieToStorage(Long storageId, AddMovieRequestDto requestDto, Long userId) throws AccessDeniedException;

    void removeMovieFromStorage(Long storageId, Long movieId, Long userId) throws AccessDeniedException;

    void updateStorageName(Long storageId, String newName, Long userId) throws AccessDeniedException;

    void updateStorageOverview(Long storageId, String newOverview, Long userId) throws AccessDeniedException;
}
