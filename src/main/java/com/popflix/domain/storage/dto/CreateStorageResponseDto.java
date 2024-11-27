package com.popflix.domain.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateStorageResponseDto {
    private Long id;
    private String storageName;
}
