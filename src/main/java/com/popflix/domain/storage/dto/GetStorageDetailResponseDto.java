package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetStorageDetailResponseDto {

    private Long id;
    private String title;
    private String posterPath;
    private String overview;
    private Long likeCount;

    public GetStorageDetailResponseDto(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.posterPath = movie.getPosterPath();
        this.overview = movie.getOverview();
        this.likeCount = movie.getLikeCount();
    }
}
