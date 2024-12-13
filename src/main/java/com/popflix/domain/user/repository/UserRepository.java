package com.popflix.domain.user.repository;

import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.enums.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userGenres WHERE u.socialId = :socialId")
    Optional<User> findBySocialId(@Param("socialId") String socialId);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByAuthTypeAndSocialId(AuthType authType, String socialId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.userGenres ug " +
            "WHERE ug.genreId IN :genreIds")
    List<User> findByGenreIds(@Param("genreIds") List<Long> genreIds);
}