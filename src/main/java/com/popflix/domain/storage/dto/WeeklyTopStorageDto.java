package com.popflix.domain.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyTopStorageDto {
    private int year;
    private int week;
    private String storageName;
    private String storageOverview;
    private Long likeCount;
    private Long movieCount;
    private String storageImage;
}
