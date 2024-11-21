package com.popflix.domain.movie.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.popflix.domain.storage.entity.MovieStorage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<MovieCast> movieCasts = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<MovieDirector> movieDirectors = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<MovieStorage> movieStorages = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<MovieLike> movieLikes = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<ReviewVideo> reviewVideos = new ArrayList<>();

    @Builder
    public Movie(String title, String overview, String posterPath, LocalDate releaseDate, List<MovieCast> movieCasts, List<MovieDirector> movieDirectors, List<MovieStorage> movieStorages, List<MovieLike> movieLikes, List<MovieGenre> movieGenres, List<Rating> ratings, List<Recommendation> recommendations, List<ReviewVideo> reviewVideos) {
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.movieCasts = movieCasts;
        this.movieDirectors = movieDirectors;
        this.movieStorages = movieStorages;
        this.movieLikes = movieLikes;
        this.movieGenres = movieGenres;
        this.ratings = ratings;
        this.recommendations = recommendations;
        this.reviewVideos = reviewVideos;
    }

}
