package com.popflix.global.event.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewCreatedEvent {
    private final Long reviewId;
    private final Long movieId;
    private final List<Long> genreIds;
    private final LocalDateTime createdAt;
    private final String reviewContent;
    private final String movieTitle;
}
