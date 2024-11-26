package com.popflix.domain.user.entity;

import com.popflix.common.entity.BaseTimeEntity;
import com.popflix.domain.movie.entity.MovieLike;
import com.popflix.domain.movie.entity.Rating;
import com.popflix.domain.movie.entity.Recommendation;
import com.popflix.domain.photoreview.entity.PhotoReviewReplyLike;
import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.storage.entity.StorageLike;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
public class User extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "social")
    private String social;

    @Column(name = "social_id")
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type")
    private AuthType authType;

    @Column(name = "admin_log_id")
    private Long adminLogId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @OneToMany(mappedBy = "user")
    private List<MovieLike> movieLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Storage> storages = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<StorageLike> storageLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PhotoReviewReplyLike> photoReviewReplyLikes = new ArrayList<>();
}
