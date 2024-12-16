package com.popflix.domain.storage.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetStorageCreatorResponseDto {

    private Long id;
    private String storageName;
    private Long movieCount;
    private Long likeCount;
    private Boolean isLiked;
    private byte[] storageImage;

    @Builder
    public GetStorageCreatorResponseDto(Long id, String storageName, Long movieCount, Long likeCount, Boolean isLiked, byte[] storageImage) {
        this.id = id;
        this.storageName = storageName;
        this.movieCount = movieCount;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.storageImage = storageImage;
    }
}