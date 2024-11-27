package com.popflix.domain.storage.service;

import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.dto.GetStorageDetailResponse;
import com.popflix.domain.storage.dto.StorageResponseDto;

import java.util.List;

public interface StorageService {
    void createStorage(CreateStorageRequestDto storageRequest);

    void changeStatus(Long storageId);

    List<StorageResponseDto> getStorageList(Long userId);

    GetStorageDetailResponse getStorageDetail(Long storageId);
}
