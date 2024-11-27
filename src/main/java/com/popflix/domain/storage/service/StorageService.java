package com.popflix.domain.storage.service;

import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.dto.CreateStorageResponseDto;

public interface StorageService {
    CreateStorageResponseDto createStorage(CreateStorageRequestDto storageRequest);

    void changeStatus(Long storageId);
}
