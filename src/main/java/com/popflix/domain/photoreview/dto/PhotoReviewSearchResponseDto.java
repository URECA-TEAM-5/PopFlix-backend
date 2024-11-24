package com.popflix.domain.photoreview.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PhotoReviewSearchResponseDto {
    private List<PhotoReviewResponseDto> photoReviews;
    private long totalCount;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
    private PhotoReviewStatistics statistics;

    @Getter
    @Builder
    public static class PhotoReviewStatistics {
        private long totalPhotoReviews;
        private double averageLikes;
        private long totalComments;
    }
}
