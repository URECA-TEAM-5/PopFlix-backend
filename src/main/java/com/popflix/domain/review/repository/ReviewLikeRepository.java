package com.popflix.domain.review.repository;

import com.popflix.domain.review.entity.Review;
import com.popflix.domain.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    @Query("SELECT rl FROM ReviewLike rl " +
            "WHERE rl.review.reviewId = :reviewId " +
            "AND rl.user.userId = :userId " +
            "AND rl.isDeleted = false")
    Optional<ReviewLike> findActiveByReviewIdAndUserId(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId);

    @Query("SELECT COUNT(rl) FROM ReviewLike rl " +
            "WHERE rl.review.reviewId = :reviewId " +
            "AND rl.isDeleted = false " +
            "AND rl.reviewLike = true")
    long countByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT rl.review FROM ReviewLike rl " +
            "WHERE rl.user.userId = :userId " +
            "AND rl.isDeleted = false " +
            "AND rl.reviewLike = true")
    List<Review> findLikedReviewsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(rl) > 0 FROM ReviewLike rl " +
            "WHERE rl.review.reviewId = :reviewId " +
            "AND rl.user.userId = :userId " +
            "AND rl.reviewLike = true " +
            "AND rl.isDeleted = false")
    boolean existsActiveByReviewIdAndUserId(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId);
}
