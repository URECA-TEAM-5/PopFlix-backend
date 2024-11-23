package com.popflix.domain.photoreview.repository;

import com.popflix.domain.photoreview.entity.PhotoReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoReviewRepository extends JpaRepository<PhotoReview, Long> {
    @Query("SELECT pr FROM PhotoReview pr " +
            "WHERE pr.reviewId = :reviewId " +
            "AND pr.isDeleted = false")
    Optional<PhotoReview> findActiveById(@Param("reviewId") Long reviewId);

    @Query("SELECT pr FROM PhotoReview pr " +
            "WHERE pr.movie.id = :movieId " +
            "AND pr.isDeleted = false")
    List<PhotoReview> findAllByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT pr FROM PhotoReview pr " +
            "WHERE pr.user.userId = :userId " +
            "AND pr.isDeleted = false")
    List<PhotoReview> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT pr FROM PhotoReview pr " +
            "WHERE pr.movie.id = :movieId " +
            "AND pr.isDeleted = false " +
            "ORDER BY pr.createAt DESC")
    Page<PhotoReview> findPageByMovieId(
            @Param("movieId") Long movieId,
            Pageable pageable
    );

    @Query("SELECT pr FROM PhotoReview pr " +
            "LEFT JOIN pr.likes l " +
            "WHERE pr.movie.id = :movieId " +
            "AND pr.isDeleted = false " +
            "GROUP BY pr " +
            "ORDER BY COUNT(l) DESC")
    Page<PhotoReview> findPageByMovieIdOrderByLikesDesc(
            @Param("movieId") Long movieId,
            Pageable pageable
    );
}

