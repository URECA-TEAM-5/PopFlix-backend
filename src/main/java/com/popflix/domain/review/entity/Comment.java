package com.popflix.domain.review.entity;

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

import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long commentId;

    @Column(length = 100, nullable = false)
    private String comment;

    @Column(nullable = false)
    private Boolean isHidden = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "comment")
    private List<CommentLike> likes = new ArrayList<>();


    @Builder
    public Comment(String comment, Review review, User user) {
        this.comment = comment;
        this.review = review;
        this.user = user;
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
