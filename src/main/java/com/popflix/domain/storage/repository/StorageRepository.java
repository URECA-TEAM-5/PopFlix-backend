package com.popflix.domain.storage.repository;

import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {
    boolean existsByUserAndStorageName(User user, String storageName);
}
