package com.popflix.domain.review.repository;

import com.popflix.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r " +
            "WHERE r.reviewId = :reviewId " +
            "AND r.isDeleted = false")
    Optional<Review> findActiveById(@Param("reviewId") Long reviewId);

    @Query("SELECT r FROM Review r " +
            "WHERE r.movie.id = :movieId " +
            "AND r.isDeleted = false")
    List<Review> findAllByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT r FROM Review r " +
            "WHERE r.isDeleted = false " +
            "ORDER BY r.createAt DESC")
    List<Review> findAllByOrderByCreateAtDesc();

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN r.likes l " +
            "WHERE r.isDeleted = false " +
            "GROUP BY r " +
            "ORDER BY COUNT(l) DESC")
    List<Review> findAllByOrderByLikesDesc();

    @Query("SELECT r FROM Review r " +
            "WHERE r.user.userId = :userId " +
            "AND r.isDeleted = false")
    List<Review> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) > 0 FROM Review r " +
            "WHERE r.movie.id = :movieId " +
            "AND r.user.userId = :userId " +
            "AND r.isDeleted = false")
    boolean existsByMovieIdAndUserId(
            @Param("movieId") Long movieId,
            @Param("userId") Long userId);
}
