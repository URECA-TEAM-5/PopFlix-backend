package com.popflix.domain.movie.service.impl;

import com.popflix.domain.movie.entity.Rating;
import com.popflix.domain.movie.exception.DuplicateRatingException;
import com.popflix.domain.movie.exception.MovieNotFoundException;
import com.popflix.domain.movie.repository.MovieRepository;
import com.popflix.domain.movie.repository.RatingRepository;
import com.popflix.domain.user.repository.UserRepository;
import com.popflix.domain.movie.service.RatingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public String addOrUpdateRating(Long userId, Long movieId, Integer score) {
        if (score < 1 || score > 5) {
            throw new DuplicateRatingException("팝콘 지수는 1점에서 5점 사이로 가능합니다.");
        }

        // 1. 영화에 평점 상태 확인
        Optional<Rating> existingRatingOpt = ratingRepository.findByUserIdAndMovieId(userId, movieId);

        if (existingRatingOpt.isPresent()) {
            // 2-1. 이미 평점이 있는 경우
            Rating existingRating = existingRatingOpt.get();

            // 일단 같은 점수일 경우 예외 추가(선택 사항)
            if (existingRating.getRating() == score) {
                throw new DuplicateRatingException("같은 평점으로는 업데이트할 수 없습니다.");
            }

            existingRating.updateRating(score);
            ratingRepository.save(existingRating);

            return "팝콘이 수정되었습니다.";

        } else {
            // 2-2. 새로운 평점을 추가하는 경우
            Rating newRating = Rating.builder()
                    .user(userRepository.getReferenceById(userId))
                    .movie(movieRepository.getReferenceById(movieId))
                    .rating(score)
                    .build();

            ratingRepository.save(newRating);

            return "새로운 팝콘이 등록되었습니다.";
        }
    }

    @Override
    @Transactional
    public String deleteRating(Long userId, Long movieId) {
        // 1. 사용자와 영화가 있는지 확인
        Rating existingRating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new IllegalArgumentException("평점이 존재하지 않습니다."));

        // 2. 평점 삭제
        existingRating.deleteRating();
        ratingRepository.delete(existingRating);

        return "평점이 삭제되었습니다.";
    }

}