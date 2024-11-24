package com.popflix.domain.photoreview.repository;

import com.popflix.domain.photoreview.entity.PhotoReviewCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoReviewCommentLikeRepository extends JpaRepository<PhotoReviewCommentLike, Long> {
    @Query("SELECT prcl FROM PhotoReviewCommentLike prcl " +
            "WHERE prcl.comment.commentId = :commentId " +
            "AND prcl.user.userId = :userId " +
            "AND prcl.isDeleted = false")
    Optional<PhotoReviewCommentLike> findActiveByCommentIdAndUserId(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(prcl) FROM PhotoReviewCommentLike prcl " +
            "WHERE prcl.comment.commentId = :commentId " +
            "AND prcl.isDeleted = false " +
            "AND prcl.commentLike = true")
    long countByCommentId(@Param("commentId") Long commentId);
}
