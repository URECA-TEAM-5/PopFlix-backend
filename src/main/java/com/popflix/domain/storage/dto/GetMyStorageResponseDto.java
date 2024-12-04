package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.storage.entity.Storage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetMyStorageResponseDto {
    private Long id;
    private String storageName;
    private Boolean isPublic;
    private byte[] storageImage;
    private Long likeCount;
    private Long movieCount;
    private Long userId;
    private List<GetMoviePosterResponseDto> movies;

    public GetMyStorageResponseDto(Storage storage, List<Movie> movies, Long userId) {
        this.id = storage.getId();
        this.storageName = storage.getStorageName();
        this.isPublic = storage.getIsPublic();
        this.storageImage = storage.getStorageImage();
        this.likeCount = storage.getLikeCount();
        this.movieCount = storage.getMovieCount();
        this.userId = storage.getUser().getUserId();
        this.movies = movies.stream()
                .map(GetMoviePosterResponseDto::new)
                .collect(Collectors.toList());
    }
}
