package com.popflix.domain.movie.repository;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.entity.MovieGenre;
import com.popflix.domain.personality.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreRepository extends JpaRepository<MovieGenre, Long> {
    boolean existsByMovieAndGenre(Movie movie, Genre genre);
}
