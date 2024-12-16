package com.popflix.domain.storage.repository;

import com.popflix.domain.storage.dto.StorageLikeCountDto;
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

    @Query(value = """
            SELECT new com.popflix.domain.storage.dto.StorageLikeCountDto(s.id, COUNT(sl.id))
            FROM Storage s
            LEFT JOIN StorageLike sl ON sl.storage = s
            WHERE sl.isLiked = true
            AND FUNCTION('YEAR', sl.createAt) = :year
            AND FUNCTION('MONTH', sl.createAt) = :month
            GROUP BY s.id
            ORDER BY COUNT(sl.id) DESC
            """)
    List<StorageLikeCountDto> findTopStoragesByLike(@Param("year") int year, @Param("month") int month);

}
