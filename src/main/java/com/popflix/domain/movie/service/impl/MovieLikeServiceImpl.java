package com.popflix.domain.movie.service.impl;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.movie.entity.MovieLike;
import com.popflix.domain.movie.repository.MovieLikeRepository;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.domain.movie.service.MovieLikeService;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.movie.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieLikeServiceImpl implements MovieLikeService {

    private final MovieRepository movieRepository;
    private final MovieLikeRepository movieLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean likeStatus(Long userId, Long movieId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        // 좋아요 추가 및 취소 처리
        Optional<MovieLike> existingLike = movieLikeRepository.findByUserIdAndMovieId(userId, movieId);
        if (existingLike.isPresent()) {
            movie.removeLike();
            movieLikeRepository.delete(existingLike.get());
            return false;
        } else {
            MovieLike movieLike = MovieLike.builder()
                    .user(user)
                    .movie(movie)
                    .isLiked(true)
                    .build();

            movie.addLike();
            movieLikeRepository.save(movieLike);
            return true;
        }
    }

    @Override
    public Map<String, Boolean> checkLikeStatus(Long movieId, Long userId) {
        Map<String, Boolean> likeStatus = new HashMap<>();

        // 좋아요 상태 확인
        boolean isLiked = movieLikeRepository.existsByMovie_IdAndUser_UserIdAndIsLiked(movieId, userId, true);
        likeStatus.put("isLiked", isLiked);

        return likeStatus;
    }
}
