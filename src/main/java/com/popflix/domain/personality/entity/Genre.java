package com.popflix.domain.personality.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.popflix.common.entity.BaseTimeEntity;
import com.popflix.domain.movie.entity.MovieGenre;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Genre extends BaseTimeEntity {
    @Id
    @JsonProperty("genre_ids")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "genre", fetch = FetchType.LAZY)
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @Builder
    public Genre(String name, List<MovieGenre> movieGenres) {
        this.name = name;
        this.movieGenres = movieGenres;
    }
}
