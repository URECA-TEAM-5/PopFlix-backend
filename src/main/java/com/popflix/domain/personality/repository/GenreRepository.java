package com.popflix.domain.personality.repository;

import com.popflix.domain.personality.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GenreRepository extends JpaRepository<Genre, Long> {

}

