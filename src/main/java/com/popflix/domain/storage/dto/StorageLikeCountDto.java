package com.popflix.domain.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StorageLikeCountDto {
    private Long storageId;
    private Long likeCount;
}
