package com.popflix.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleVideoDto {
    private Long id;
    private String url;
}
