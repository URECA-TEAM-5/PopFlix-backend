package com.popflix.domain.storage.controller;

import com.popflix.domain.movie.dto.AddMovieRequestDto;
import com.popflix.domain.movie.service.MovieService;
import com.popflix.domain.storage.dto.CreateStorageRequestDto;
import com.popflix.domain.storage.service.StorageLikeService;
import com.popflix.domain.storage.service.StorageService;
import com.popflix.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/storages")
public class StorageController {

    private final StorageService storageService;
    private final MovieService movieService;
    private final StorageLikeService storageLikeService;


    // Todo: 5. 보관함 삭제
    // Todo: 6. 보관함 수정(영화 추가 or 영화 삭제/ 보관함 명 수정/ 보관함 소개글 수정) - 프론트랑 얘기 필요
    // Todo: 7. 보관함 에 영화 추가시 중복 추가 안되게 구현
    // Todo: 9. 보관함 목록/상세 조회 필드 수정 ( 상세 조회 시 띄어진 필드 감독인지 출연진인지 프론트랑 얘기 )
    // Todo: 10. 보관함 목록 인기순/최신순 기능
    // Todo: 11. 영화 목록/상세 조회 시에도 좋아요 여부 보이게 해야할 듯


    // 보관함 생성
    @PostMapping("/create")
    public ApiUtil.ApiSuccess<?> createStorage(@RequestBody CreateStorageRequestDto storageRequest) {
        storageService.createStorage(storageRequest);
        return ApiUtil.success("보관함이 생성되었습니다.");
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
    @GetMapping("/{userId}")
    public ApiUtil.ApiSuccess<?> getStorageList(@PathVariable Long userId) {
        return ApiUtil.success(storageService.getStorageList(userId));
    }

    // 보관함 상세 조회
    @GetMapping("/{storageId}/details")
    public ApiUtil.ApiSuccess<?> getStorageDetail(@PathVariable Long storageId, @RequestParam Long userId) {
        return ApiUtil.success(storageService.getStorageDetail(storageId, userId));
    }

    // 영화 좋아요 추가 & 취소
    @PostMapping("/{storageId}/like")
    public ApiUtil.ApiSuccess<?> storageLike(
            @PathVariable Long storageId,
            @RequestParam Long userId
    ) {
        boolean isLiked = storageLikeService.storageLike(userId, storageId);
        String message = isLiked ? "보관함 좋아요를 추가했습니다." : "보관함 좋아요를 취소했습니다.";
        return ApiUtil.success(message);
    }

    // 영화 좋아요 상태 조회
    @GetMapping("/like")
    public ApiUtil.ApiSuccess<?> getLikeStatus(
            @RequestParam Long storageId,
            @RequestParam Long userId
    ){
        Map<String, Boolean> likeStatus = storageLikeService.checkLikeStatus(storageId, userId);

        return ApiUtil.success(likeStatus);
    }

}
