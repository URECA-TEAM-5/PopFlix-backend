package com.popflix.domain.user.repository;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findBySocialId(String socialId);

    Optional<User> findByNickname(String nickname);

    // 소셜 로그인 (플랫폼과 ID로 검색)
    Optional<User> findByAuthTypeAndSocialId(AuthType authType, String socialId);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN UserGenre ug ON u.userId = ug.userId " +
            "WHERE ug.genreId IN :genreIds")
    List<User> findByGenreIds(@Param("genreIds") List<Long> genreIds);
}