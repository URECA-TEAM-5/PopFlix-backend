package com.popflix.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewSearchResponseDto {
    private List<ReviewResponseDto> reviews;
    private long totalCount;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
    private ReviewStatistics statistics;

    @Getter
    @Builder
    public static class ReviewStatistics {
        private long totalReviews;
        private double averageLikes;
        private long totalComments;
    }
}
