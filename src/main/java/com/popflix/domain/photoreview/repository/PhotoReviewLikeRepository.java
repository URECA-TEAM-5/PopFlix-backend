package com.popflix.domain.photoreview.repository;

import com.popflix.domain.photoreview.entity.PhotoReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoReviewLikeRepository extends JpaRepository<PhotoReviewLike, Long> {
    @Query("SELECT prl FROM PhotoReviewLike prl " +
            "WHERE prl.photoReview.reviewId = :reviewId " +
            "AND prl.user.userId = :userId " +
            "AND prl.isDeleted = false")
    Optional<PhotoReviewLike> findActiveByReviewIdAndUserId(
            @Param("reviewId") Long reviewId,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(prl) FROM PhotoReviewLike prl " +
            "WHERE prl.photoReview.reviewId = :reviewId " +
            "AND prl.isDeleted = false " +
            "AND prl.reviewLike = true")
    long countByReviewId(@Param("reviewId") Long reviewId);
}