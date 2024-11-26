package com.popflix.domain.photoreview.entity;

import com.popflix.common.entity.BaseSoftDeleteEntity;
import com.popflix.common.entity.BaseTimeEntity;
import com.popflix.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoReviewReplyLike extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyLikeId;

    @Column(nullable = false)
    private Boolean replyLike = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reply_id", nullable = false)
    private PhotoReviewReply reply;

    @Builder
    public PhotoReviewReplyLike(User user, PhotoReviewReply reply) {
        this.user = user;
        this.reply = reply;
        this.replyLike = true;
    }

    public void toggleLike() {
        this.replyLike = !this.replyLike;
    }
}