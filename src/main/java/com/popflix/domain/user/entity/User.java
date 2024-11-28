package com.popflix.domain.user.entity;

import com.popflix.common.entity.BaseTimeEntity;
import com.popflix.domain.movie.entity.MovieLike;
import com.popflix.domain.movie.entity.Rating;
import com.popflix.domain.movie.entity.Recommendation;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.enums.Gender;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@Builder
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image")
    private String profileImage; // 프로필 이미지를 저장하는 필드 (S3 URL을 저장할 수 있음)

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false)
    private AuthType authType; // 소셜 로그인 타입 (GOOGLE, NAVER, NONE)

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Column(name = "admin_log_id")
    private Long adminLogId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    // 연관관계 매핑
    @OneToMany(mappedBy = "user")
    private List<MovieLike> movieLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Recommendation> recommendations = new ArrayList<>();
}