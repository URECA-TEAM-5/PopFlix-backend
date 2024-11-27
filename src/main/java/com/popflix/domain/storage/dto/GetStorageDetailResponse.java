package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.storage.entity.Storage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetStorageDetailResponse {

    private StorageResponseDto storage;
    private List<GetStorageDetailResponseDto> movies;

    public GetStorageDetailResponse(Storage storage, List<Movie> movies) {
        this.storage = new StorageResponseDto(storage);
        this.movies = movies.stream()
                .map(GetStorageDetailResponseDto::new)
                .collect(Collectors.toList());
    }
}
