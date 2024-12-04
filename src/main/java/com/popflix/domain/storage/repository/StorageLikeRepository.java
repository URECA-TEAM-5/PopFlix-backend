package com.popflix.domain.storage.repository;

import com.popflix.domain.storage.entity.StorageLike;
import com.popflix.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StorageLikeRepository extends JpaRepository<StorageLike, Long> {
    @Query(value = """
            SELECT sl
            FROM StorageLike sl
            WHERE sl.user.id = :userId AND sl.storage.id = :storageId
            """)
    Optional<StorageLike> findByUserIdAndStorageId(@Param("userId") Long userId, @Param("storageId") Long storageId);

    boolean existsByStorage_IdAndUser_UserIdAndIsLiked(Long storageId, Long userId, boolean b);

    List<StorageLike> findByUserAndIsLikedTrue(User user);
}
