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
public class PhotoReviewLike extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewLikeId;

    @Column(nullable = false)
    private Boolean reviewLike = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private PhotoReview photoReview;

    @Builder
    public PhotoReviewLike(User user, PhotoReview photoReview) {
        this.user = user;
        this.photoReview = photoReview;
        this.reviewLike = true;
    }

    public void toggleLike() {
        this.reviewLike = !this.reviewLike;
    }
}