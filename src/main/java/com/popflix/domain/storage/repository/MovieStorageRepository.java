package com.popflix.domain.storage.repository;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.storage.entity.MovieStorage;
import com.popflix.domain.storage.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieStorageRepository extends JpaRepository<MovieStorage, Long> {

    @Query(value = """
            SELECT ms.movie
              FROM MovieStorage ms
             WHERE ms.storage.id = :storageId
            """)
    List<Movie> findMoviesByStorageId(@Param("storageId") Long storageId);

    boolean existsByStorageAndMovie(Storage storage, Movie movie);

    Optional<MovieStorage> findByStorageAndMovieId(Storage storage, Long movieId);
}
