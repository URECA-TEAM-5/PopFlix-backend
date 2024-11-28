package com.popflix.domain.storage.service;

import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.dto.GetStorageDetailResponseDto;
import com.popflix.domain.storage.dto.GetStorageResponseDto;

import java.util.List;

public interface StorageService {
    void createStorage(CreateStorageRequestDto storageRequest);

    void changeStatus(Long storageId);

    List<GetStorageResponseDto> getStorageList(Long userId, String sort);

    GetStorageDetailResponseDto getStorageDetail(Long storageId, Long userId);
}
