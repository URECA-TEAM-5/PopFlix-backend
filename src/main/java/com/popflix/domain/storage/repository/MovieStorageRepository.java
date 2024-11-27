package com.popflix.domain.storage.repository;

import com.popflix.domain.storage.entity.MovieStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieStorageRepository extends JpaRepository<MovieStorage, Long> {
}
