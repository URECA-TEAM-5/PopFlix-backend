package com.popflix.domain.user.repository;

import com.popflix.domain.user.entity.UserGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserGenreRepository extends JpaRepository<UserGenre, Long> {
    @Query("SELECT ug.genreId FROM UserGenre ug WHERE ug.user = :userId")
    Long findGenreIdByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserGenre ug WHERE ug.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}