package com.popflix.domain.user.repository;

import com.popflix.domain.user.entity.UserGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserGenreRepository extends JpaRepository<UserGenre, Long> {
    @Query(value = """
            SELECT ug.genreId
              FROM UserGenre ug
             WHERE ug.userId = :userId
            """)
    Long findGenreIdByUserId(@Param("userId") Long userId);
}