package com.popflix.domain.movie.repository;

import com.popflix.domain.movie.entity.MovieDirector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieDirectorRepository extends JpaRepository<MovieDirector, Long> {
}
