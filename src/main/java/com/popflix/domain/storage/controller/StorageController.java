package com.popflix.domain.storage.controller;

import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.movie.service.MovieLikeService;
import com.popflix.domain.movie.service.MovieService;
import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.service.StorageService;
import com.popflix.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/storages")
public class StorageController {

    private final StorageService storageService;
    private final MovieService movieService;
    private final MovieLikeService movieLikeService;


    // Todo: 5. 보관함 삭제
    // Todo: 6. 보관함 수정
    // Todo: 7. 보관함 에 영화 추가시 중복 추가 안되게 구현


    // 보관함 생성
    @PostMapping("/create")
    public ApiUtil.ApiSuccess<?> createStorage(@RequestBody CreateStorageRequestDto storageRequest) {
        return ApiUtil.success(storageService.createStorage(storageRequest));
    }

    // 보관함 공개 여부 스위치
    @PatchMapping("/{storageId}/switch")
    public ApiUtil.ApiSuccess<?> switchIsPublic(@PathVariable Long storageId) {
        storageService.changeStatus(storageId);
        return ApiUtil.success("보관함의 공개 여부가 변경되었습니다.");
    }

    // 보관함에 영화 추가
    @PostMapping("/add-movie/{storageId}")
    public ApiUtil.ApiSuccess<?> addMovieToStorage(@PathVariable Long storageId, @RequestBody AddMovieRequestDto movieRequest) {
        movieService.addMovieToStorage(storageId, movieRequest);
        return ApiUtil.success("영화가 추가되었습니다.");
    }

    // 보관함 목록 조회
    @GetMapping
    public ApiUtil.ApiSuccess<?> getStorageList() {
        return ApiUtil.success(storageService.getStorageList());
    }

    // 보관함 상세 조회
    @GetMapping("/{storageId}")
    public ApiUtil.ApiSuccess<?> getStorageDetail(@PathVariable Long storageId) {
        return ApiUtil.success(storageService.getStorageDetail(storageId));
    }

}
