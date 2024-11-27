package com.popflix.domain.storage.dto;

import com.popflix.domain.storage.entity.Storage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GetStorageDetail2ResponseDto {
    private Long id;
    private String storageName;
    private String username;
    private LocalDate updateAt;
    private Long likeCount;
    private Boolean isLiked;
    private String overview;
    private byte[] storageImage;

    public GetStorageDetail2ResponseDto(Storage storage, Long userId, boolean isLiked) {
        this.id = storage.getId();
        this.storageName = storage.getStorageName();
        this.username = storage.getUser().getName();
        this.updateAt = LocalDate.from(storage.getUpdateAt());
        this.likeCount = storage.getLikeCount();
        this.isLiked = isLiked;  // 좋아요 상태
        this.overview = storage.getStorageOverview();
        this.storageImage = storage.getStorageImage();
    }
}
