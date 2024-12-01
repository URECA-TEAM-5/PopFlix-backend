package com.popflix.domain.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStorageRequestDto {
    private String newName;
    private String newOverview;
}
