package com.popflix.domain.photoreview.repository;

import com.popflix.domain.photoreview.entity.PhotoReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoReviewCommentRepository extends JpaRepository<PhotoReviewComment, Long> {
    @Query("SELECT prc FROM PhotoReviewComment prc " +
            "WHERE prc.commentId = :commentId " +
            "AND prc.isDeleted = false")
    Optional<PhotoReviewComment> findActiveById(@Param("commentId") Long commentId);

    @Query("SELECT prc FROM PhotoReviewComment prc " +
            "WHERE prc.photoReview.reviewId = :reviewId " +
            "AND prc.isDeleted = false " +
            "ORDER BY prc.createAt ASC")
    List<PhotoReviewComment> findAllByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT prc FROM PhotoReviewComment prc " +
            "WHERE prc.user.userId = :userId " +
            "AND prc.isDeleted = false")
    List<PhotoReviewComment> findAllByUserId(@Param("userId") Long userId);
}
