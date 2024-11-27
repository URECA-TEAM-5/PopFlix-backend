package com.popflix.domain.storage.service.impl;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.entity.MovieLike;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.movie.exception.UserNotFoundException;
import com.popflix.domain.movie.repository.MovieLikeRepository;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.storage.entity.StorageLike;
import com.popflix.domain.storage.exception.StorageNotFoundException;
import com.popflix.domain.storage.repository.StorageLikeRepository;
import com.popflix.domain.storage.repository.StorageRepository;
import com.popflix.domain.storage.service.StorageLikeService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageLikeServiceImpl implements StorageLikeService {

    private final UserRepository userRepository;
    private final StorageRepository storageRepository;
    private final StorageLikeRepository storageLikeRepository;

    @Transactional
    @Override
    public boolean storageLike(Long userId, Long storageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Storage storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new StorageNotFoundException(storageId));

        // 좋아요 추가 및 취소 처리
        Optional<StorageLike> existingLike = storageLikeRepository.findByUserIdAndStorageId(userId, storageId);
        if (existingLike.isPresent()) {
            storage.removeStorageLike();
            storageLikeRepository.delete(existingLike.get());
            return false;
        } else {
            StorageLike storageLike = StorageLike.builder()
                    .user(user)
                    .storage(storage)
                    .isLiked(true)
                    .build();

            storage.addStorageLike();
            storageLikeRepository.save(storageLike);
            return true;
        }
    }

    @Override
    public Map<String, Boolean> checkLikeStatus(Long storageId, Long userId) {
        Map<String, Boolean> likeStatus = new HashMap<>();

        // 좋아요 상태 확인
        boolean isLiked = storageLikeRepository.existsByStorage_IdAndUser_UserIdAndIsLiked(storageId, userId, true);
        likeStatus.put("isLiked", isLiked);

        return likeStatus;
    }

}
