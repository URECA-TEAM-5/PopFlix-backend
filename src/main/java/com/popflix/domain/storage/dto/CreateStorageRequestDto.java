package com.popflix.domain.storage.dto;

import com.popflix.domain.storage.entity.Storage;
import com.popflix.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateStorageRequestDto {
    private  Long storageId;
    private String storageName;
    private String storageOverview;
    private String storageImage;
    private Long userId;

    public static CreateStorageRequestDto from(final Storage storage) {
        final CreateStorageRequestDto createStorageRequestDto = new CreateStorageRequestDto();

        createStorageRequestDto.storageId = storage.getId();
        createStorageRequestDto.storageName = storage.getStorageName();
        createStorageRequestDto.storageOverview = storage.getStorageOverview();
        createStorageRequestDto.storageImage = storage.getStorageImage();
        User user = storage.getUser();
        createStorageRequestDto.userId = user.getUserId();

        return createStorageRequestDto;
    }
}
