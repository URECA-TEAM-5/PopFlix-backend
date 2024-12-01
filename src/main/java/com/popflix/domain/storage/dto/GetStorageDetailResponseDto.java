package com.popflix.domain.storage.dto;

import com.popflix.domain.movie.entity.Movie;
import com.popflix.domain.storage.entity.Storage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class GetStorageDetailResponseDto {

    private GetStorageDetail2ResponseDto storage;  // 보관함 정보
    private List<GetStorageMovieResponseDto> movies; // 영화 목록

    public GetStorageDetailResponseDto(Storage storage, List<Movie> movies, Long userId, boolean isLiked) {
        this.storage = new GetStorageDetail2ResponseDto(storage, userId, isLiked);

        this.movies = movies.stream()
                .map(GetStorageMovieResponseDto::new)
                .collect(Collectors.toList());
    }
}

/*
1. 보관함 명
2. 만든이
3. 만든 날짜
4. 보관함 좋아요 수
5. 보관함 좋아요 조회
6. 보관함 소개글
7. 보관함 이미지
8. 영화 리스트
    8-1. 영화 포스터
    8-2. 영화 제목
    8-3. 장르
    8-4. 감독(?)
    8-5. 개봉일
 */