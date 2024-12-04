package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.storage.entity.Storage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetLikedStorageResponseDto {

    private Long id;
    private String storageName;
    private String creatorNickname;
    private Long movieCount;
    private boolean isLiked;
    private List<GetLikedStorageInfoResponseDto> movies;

    public GetLikedStorageResponseDto(Storage storage, boolean isLiked) {
        this.id = storage.getId();
        this.storageName = storage.getStorageName();
        this.creatorNickname = storage.getUser().getNickname();
        this.movieCount = storage.getMovieCount();
        this.isLiked = isLiked;
        this.movies = storage.getMovies().stream()
                .map(GetLikedStorageInfoResponseDto::new)
                .collect(Collectors.toList());
    }
}
