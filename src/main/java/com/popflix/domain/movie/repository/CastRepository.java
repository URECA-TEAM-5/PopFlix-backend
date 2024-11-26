package com.popflix.domain.movie.repository;

import com.popflix.domain.movie.entity.Cast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CastRepository extends JpaRepository<Cast, Long> {
    Optional<Cast> findByName(String name);
}
