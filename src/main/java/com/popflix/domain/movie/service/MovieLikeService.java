package com.popflix.domain.movie.service;

import java.util.Map;

public interface MovieLikeService {
    boolean likeStatus(Long userId, Long movieId);

    Map<String, Boolean> checkLikeStatus(Long movieId, Long userId);
}
