package com.popflix.domain.review.repository;

import com.popflix.domain.review.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c " +
            "WHERE c.commentId = :commentId " +
            "AND c.isDeleted = false")
    Optional<Comment> findActiveById(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.review.reviewId = :reviewId " +
            "AND c.isDeleted = false")
    List<Comment> findAllByReviewId(@Param("reviewId") Long reviewId);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.user.userId = :userId " +
            "AND c.isDeleted = false")
    List<Comment> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.review.reviewId = :reviewId " +
            "AND c.isDeleted = false " +
            "ORDER BY c.createAt ASC")
    List<Comment> findAllByReviewIdOrderByCreateAtAsc(@Param("reviewId") Long reviewId);
}
