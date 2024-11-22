package com.popflix.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderType {
    LATEST("최신순"),
    LIKE("좋아요순");

    private final String description;
}
