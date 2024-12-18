package com.popflix.domain.storage.service;

import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.storage.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    void createStorage(CreateStorageRequestDto storageRequest);

    void changeStatus(Long storageId, Long userId);

    List<GetStorageResponseDto> getStorageList(Long currentUserId, String sort);

    GetStorageDetailResponseDto getStorageDetail(Long storageId, Long userId);

    List<GetStorageCreatorResponseDto> getOtherStoragesByCreator(Long storageId, Long userId);

    void addMovieToStorage(Long storageId, AddMovieRequestDto requestDto, Long userId);

    void removeMovieFromStorage(Long storageId, Long movieId, Long userId);

    void deleteStorage(Long storageId, Long userId);

    void updateStorageDetails(Long storageId, String newName, String newOverview, MultipartFile storageImage, Long userId) throws IOException;

    List<GetMyStorageResponseDto> getStoragesByCreator(Long userId);

    List<GetLikedStorageResponseDto> getLikedStorages(Long userId);

    List<MonthlyTopStorageDto> getMonthlyTopStorages(int year, int month);
}
