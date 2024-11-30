package com.popflix.domain.movie.repository;

import com.popflix.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("""
                SELECT m FROM Movie m
                LEFT JOIN m.movieCasts mc
                LEFT JOIN mc.cast c
                LEFT JOIN m.movieDirectors md
                LEFT JOIN md.director d
                WHERE m.title LIKE CONCAT('%', :keyword, '%')
                OR c.name LIKE CONCAT('%', :keyword, '%')
                OR d.name LIKE CONCAT('%', :keyword, '%')
                ORDER BY CASE
                    WHEN m.title LIKE CONCAT('%', :keyword, '%') THEN 0
                    WHEN c.name LIKE CONCAT('%', :keyword, '%') THEN 1
                    WHEN d.name LIKE CONCAT('%', :keyword, '%') THEN 2
                    ELSE 3 END, m.id ASC
            """)
    Page<Movie> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("""
                SELECT m FROM Movie m
                JOIN m.movieGenres mg
                JOIN mg.genre g
                WHERE g.name = :genre
            """)
    Page<Movie> findByGenre(@Param("genre") String genre, Pageable pageable);


    @Query(value = """
            SELECT m
              FROM Movie m
             ORDER BY m.id ASC
            """)
    Page<Movie> findAllMovieInfo(Pageable pageable);

    Optional<Movie> findByTitle(String title);
}
