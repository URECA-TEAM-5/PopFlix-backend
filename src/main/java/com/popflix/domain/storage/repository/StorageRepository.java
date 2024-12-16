package com.popflix.domain.storage.repository;

import com.popflix.domain.storage.dto.WeeklyTopStorageDto;
import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {
    boolean existsByUserAndStorageName(User user, String storageName);

    List<Storage> findByUserAndIdNot(User creator, Long storageId);

    List<Storage> findByUser(User user);

    @Query(value = """
    SELECT new com.popflix.domain.storage.dto.WeeklyTopStorageDto(
        YEAR(s.createAt), WEEK(s.createAt),
        s.storageName, s.storageOverview,
        s.likeCount, s.movieCount, s.storageImage
    )
    FROM Storage s
    WHERE s.isDeleted = false AND s.isPublic = true
    GROUP BY YEAR(s.createAt), WEEK(s.createAt),
             s.storageName, s.storageOverview,
             s.likeCount, s.movieCount, s.storageImage, s.id
    ORDER BY YEAR(s.createAt) DESC, WEEK(s.createAt) DESC, s.likeCount DESC
    """)
    List<WeeklyTopStorageDto> findWeeklyTopStorages();


    List<Storage> findByUserAndIsDeletedFalse(User user);
}
