package com.popflix.domain.movie.dto;

import com.popflix.domain.movie.entity.Cast;
import com.popflix.domain.movie.entity.Director;
import com.popflix.domain.movie.entity.ReviewVideo;
import com.popflix.domain.personality.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleDto {
    private Long id;
    private String name;

    public static SimpleDto fromCast(Cast cast) {
        return new SimpleDto(cast.getId(), cast.getName());
    }

    public static SimpleDto fromDirector(Director director) {
        return new SimpleDto(director.getId(), director.getName());
    }

    public static SimpleDto fromGenre(Genre genre) {
        return new SimpleDto(genre.getId(), genre.getName());
    }

    public static SimpleDto fromReviewVideo(ReviewVideo reviewVideo) {
        return new SimpleDto(reviewVideo.getId(), reviewVideo.getLink());
    }
}
