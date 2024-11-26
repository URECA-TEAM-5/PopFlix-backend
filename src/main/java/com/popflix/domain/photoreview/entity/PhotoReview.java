package com.popflix.domain.photoreview.entity;

import com.popflix.common.entity.BaseSoftDeleteEntity;
import com.popflix.common.entity.BaseTimeEntity;
import com.popflix.domain.movie.entity.Movie;
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
public class PhotoReview extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(length = 100, nullable = false)
    private String review;

    @Lob
    @Column(nullable = false)
    private byte[] reviewImage;

    @Column(nullable = false)
    private Boolean isHidden = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "photoReview")
    private List<PhotoReviewComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "photoReview")
    private List<PhotoReviewLike> likes = new ArrayList<>();

    @Builder
    public PhotoReview(String review, byte[] reviewImage, Movie movie, User user) {
        this.review = review;
        this.reviewImage = reviewImage;
        this.movie = movie;
        this.user = user;
        this.isHidden = false;
    }

    public void updateReview(String review) {
        this.review = review;
    }

    public void updateImage(byte[] reviewImage) {
        this.reviewImage = reviewImage;
    }

    public void hide() {
        this.isHidden = true;
    }

    public void unhide() {
        this.isHidden = false;
    }
}
