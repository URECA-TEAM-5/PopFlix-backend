package com.popflix.domain.movie.service;

public interface RatingService {
    String addOrUpdateRating(Long userId, Long movieId, Integer score);
}
