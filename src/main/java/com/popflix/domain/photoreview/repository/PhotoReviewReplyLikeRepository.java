package com.popflix.domain.photoreview.repository;

import com.popflix.domain.photoreview.entity.PhotoReviewReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhotoReviewReplyLikeRepository extends JpaRepository<PhotoReviewReplyLike, Long> {
    // 특정 대댓글 좋아요 여부 확인
    @Query("SELECT prrl FROM PhotoReviewReplyLike prrl " +
            "WHERE prrl.reply.replyId = :replyId " +
            "AND prrl.user.userId = :userId " +
            "AND prrl.isDeleted = false")
    Optional<PhotoReviewReplyLike> findActiveByReplyIdAndUserId(
            @Param("replyId") Long replyId,
            @Param("userId") Long userId
    );

    // 특정 대댓글의 좋아요 수 카운트
    @Query("SELECT COUNT(prrl) FROM PhotoReviewReplyLike prrl " +
            "WHERE prrl.reply.replyId = :replyId " +
            "AND prrl.isDeleted = false " +
            "AND prrl.replyLike = true")
    long countByReplyId(@Param("replyId") Long replyId);
}
