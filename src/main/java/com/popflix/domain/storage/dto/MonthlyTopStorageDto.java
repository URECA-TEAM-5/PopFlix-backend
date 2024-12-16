package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.storage.entity.Storage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTopStorageDto {
    private Long id; // 보관함 ID 추가
    private String storageName;
    private String storageOverview;
    private Long likeCount;
    private Long movieCount;
    private byte[] storageImage;
    private List<GetMoviePosterResponseDto> movies; // 영화 목록 추가

    public MonthlyTopStorageDto(Storage storage, List<Movie> movies) {
        this.id = storage.getId();
        this.storageName = storage.getStorageName();
        this.storageOverview = storage.getStorageOverview();
        this.likeCount = storage.getLikeCount();
        this.movieCount = storage.getMovieCount();
        this.storageImage = storage.getStorageImage();
        this.movies = movies.stream()
                .map(GetMoviePosterResponseDto::new)
                .collect(Collectors.toList());
    }
}

