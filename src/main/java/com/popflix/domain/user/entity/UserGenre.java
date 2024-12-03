package com.popflix.domain.user.entity;

import com.popflix.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "User_Genre")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGenre extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_genre_id")
    private Long userGenreId;

    @Column(name = "genre_id")
    private Long genreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public UserGenre(Long genreId, User user) {
        this.genreId = genreId;
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}