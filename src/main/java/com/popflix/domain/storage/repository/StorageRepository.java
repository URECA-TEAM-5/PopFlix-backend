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
    List<Storage> findByUserAndIdNot(User creator, Long storageId);

    @Query(value = "SELECT * FROM storage " +
            "WHERE is_deleted = 0 " +
            "AND YEAR(create_at) = :year " +
            "AND MONTH(create_at) = :month " +
            "ORDER BY like_count DESC", nativeQuery = true)
    List<Storage> findMonthlyTopStorages(@Param("year") int year, @Param("month") int month);


    List<Storage> findByUserAndIsDeletedFalse(User user);
}
