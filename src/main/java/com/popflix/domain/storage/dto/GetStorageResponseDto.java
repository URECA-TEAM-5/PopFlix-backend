package com.popflix.domain.storage.dto;

import com.popflix.domain.storage.entity.Storage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetStorageResponseDto {

    private Long id;
    private String storageName;
    private String username;
    private Long movieCount;
    private Long likeCount;
    private Boolean isLiked;
    private byte[] storageImage;

    @Builder
    public GetStorageResponseDto(Long id, String storageName, String username, Long movieCount, Long likeCount, Boolean isLiked, byte[] storageImage) {
        this.id = id;
        this.storageName = storageName;
        this.username = username;
        this.movieCount = movieCount;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.storageImage = storageImage;
    }

    public GetStorageResponseDto(Storage savedStorage) {
    }
}

/*
1. 보관함 명
2. 만든이
3. 총 영화 수
4. 보관함 좋아요 수
5. 보관함 좋아요 조회
6. 보관함 이미지
 */