package com.popflix.domain.user.repository;

import com.popflix.domain.user.entity.UserGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGenreRepository extends JpaRepository<UserGenre, Long> {
    @Query("SELECT ug.genreId FROM UserGenre ug WHERE ug.user = :userId")
    Long findGenreIdByUserId(@Param("userId") Long userId);
    @Query("DELETE FROM UserGenre ug WHERE ug.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}