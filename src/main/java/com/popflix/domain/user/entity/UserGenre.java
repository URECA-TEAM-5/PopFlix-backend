package com.popflix.domain.user.entity;

import com.popflix.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "User_Genre")
@Getter
public class UserGenre extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "genre_id")
    private Long genreId;

    @Column(name = "user_id")
    private Long userId;
}
