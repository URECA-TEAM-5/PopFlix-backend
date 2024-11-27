package com.popflix.domain.storage.service;

import java.util.Map;

public interface StorageLikeService {
    boolean storageLike(Long userId, Long storageId);

    Map<String, Boolean> checkLikeStatus(Long storageId, Long userId);
}
