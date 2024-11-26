package com.popflix.domain.movie.repository;

import com.popflix.domain.movie.entity.MovieCast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {
}
