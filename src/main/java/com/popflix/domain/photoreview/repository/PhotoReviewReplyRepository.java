package com.popflix.domain.photoreview.repository;

import com.popflix.domain.photoreview.entity.PhotoReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoReviewReplyRepository extends JpaRepository<PhotoReviewReply, Long> {
    @Query("SELECT prr FROM PhotoReviewReply prr " +
            "WHERE prr.replyId = :replyId " +
            "AND prr.isDeleted = false")
    Optional<PhotoReviewReply> findActiveById(@Param("replyId") Long replyId);

    @Query("SELECT prr FROM PhotoReviewReply prr " +
            "WHERE prr.comment.commentId = :commentId " +
            "AND prr.isDeleted = false " +
            "ORDER BY prr.createAt ASC")
    List<PhotoReviewReply> findAllByCommentId(@Param("commentId") Long commentId);
}
