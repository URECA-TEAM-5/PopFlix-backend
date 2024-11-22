package com.popflix.domain.review.repository;

import com.popflix.domain.review.entity.Comment;
import com.popflix.domain.review.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    @Query("SELECT cl FROM CommentLike cl " +
            "WHERE cl.comment.commentId = :commentId " +
            "AND cl.user.userId = :userId " +
            "AND cl.isDeleted = false")
    Optional<CommentLike> findActiveByCommentIdAndUserId(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId);

    @Query("SELECT COUNT(cl) FROM CommentLike cl " +
            "WHERE cl.comment.commentId = :commentId " +
            "AND cl.commentLike = true " +
            "AND cl.isDeleted = false")
    long countByCommentId(@Param("commentId") Long commentId);

    @Query("SELECT cl.comment FROM CommentLike cl " +
            "WHERE cl.user.userId = :userId " +
            "AND cl.isDeleted = false " +
            "AND cl.commentLike = true")
    List<Comment> findLikedCommentsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(cl) > 0 FROM CommentLike cl " +
            "WHERE cl.comment.commentId = :commentId " +
            "AND cl.user.userId = :userId " +
            "AND cl.commentLike = true " +
            "AND cl.isDeleted = false")
    boolean existsActiveByCommentIdAndUserId(
            @Param("commentId") Long commentId,
            @Param("userId") Long userId);
}
