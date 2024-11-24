package com.popflix.domain.photoreview.entity;

import com.popflix.common.entity.BaseSoftDeleteEntity;
import com.popflix.common.entity.BaseTimeEntity;
import com.popflix.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoReviewComment extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(length = 100, nullable = false)
    private String comment;

    @Column(nullable = false)
    private Boolean isHidden = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private PhotoReview photoReview;

    @OneToMany(mappedBy = "comment")
    private List<PhotoReviewCommentLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<PhotoReviewReply> replies = new ArrayList<>();

    @Builder
    public PhotoReviewComment(String comment, User user, PhotoReview photoReview) {
        this.comment = comment;
        this.user = user;
        this.photoReview = photoReview;
        this.isHidden = false;
    }

    public void updateComment(String comment) {
        this.comment = comment;
    }

    public void hide() {
        this.isHidden = true;
    }

    public void unhide() {
        this.isHidden = false;
    }
}
