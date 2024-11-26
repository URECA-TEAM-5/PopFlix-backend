package com.popflix.domain.movie.repository;

import com.popflix.domain.movie.entity.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {
    @Query(value = """
            SELECT ml
              FROM MovieLike ml
             WHERE ml.user.id = :userId AND ml.movie.id = :movieId
            """)
    Optional<MovieLike> findByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

    boolean existsByMovie_IdAndUser_UserIdAndIsLiked(Long movieId, Long userId, boolean isLiked);
}
