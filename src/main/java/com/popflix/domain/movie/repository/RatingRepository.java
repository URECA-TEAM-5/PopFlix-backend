package com.popflix.domain.movie.repository;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.entity.Rating;
import com.popflix.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query(value = """
    SELECT AVG(r.rating)
      FROM Rating r
     WHERE r.movie.id = :movieId
    """)
    Double findAverageRatingByMovieId(@Param("movieId") Long movieId);

    @Query(value = """
    SELECT r
      FROM Rating r
     WHERE r.movie.id = :movieId
    """)
    List<Rating> findAllRatingsByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT r FROM Rating r WHERE r.user.id = :userId AND r.movie.id = :movieId")
    Optional<Rating> findByUserIdAndMovieId(@Param("userId") Long userId, @Param("movieId") Long movieId);

}
