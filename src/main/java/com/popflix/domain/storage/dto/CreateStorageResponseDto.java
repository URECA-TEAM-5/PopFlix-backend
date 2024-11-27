package com.popflix.domain.storage.dto;

import com.popflix.domain.storage.entity.Storage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateStorageResponseDto {

    private Long id;
    private String storageName;
    private String storageOverview;
    private byte[] storageImage;
    private Long likeCount;
    private Long movieCount;

    public CreateStorageResponseDto(Storage storage) {
        this.id = storage.getId();
        this.storageName = storage.getStorageName();
        this.storageOverview = storage.getStorageOverview();
        this.storageImage = storage.getStorageImage();
        this.likeCount = storage.getLikeCount();
        this.movieCount = storage.getMovieCount();
    }
}
