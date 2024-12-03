package com.popflix.domain.user.entity;

import com.popflix.common.entity.BaseTimeEntity;
import com.popflix.domain.movie.entity.MovieLike;
import com.popflix.domain.movie.entity.Rating;
import com.popflix.domain.movie.entity.Recommendation;
import com.popflix.domain.user.enums.AuthType;
import com.popflix.domain.user.enums.Gender;
import com.popflix.domain.user.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false)
    private AuthType authType;

    @Column(name = "social_id", unique = true)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private List<UserGenre> userGenres = new ArrayList<>();

    @Builder
    public User(String email, String name, String nickname, String profileImage,
                AuthType authType, String socialId, Role role, Gender gender) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.authType = authType;
        this.socialId = socialId;
        this.role = role;
        this.gender = gender;
    }

    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void addUserGenre(UserGenre userGenre) {
        this.userGenres.add(userGenre);
        if (userGenre.getUser() != this) {
            userGenre.setUser(this);
        }
    }
}