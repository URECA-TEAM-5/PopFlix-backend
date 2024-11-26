package com.popflix.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LikeType {
    REVIEW("리뷰"),
    COMMENT("댓글");

    private final String description;
}
