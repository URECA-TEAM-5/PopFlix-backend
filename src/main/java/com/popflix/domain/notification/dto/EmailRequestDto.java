package com.popflix.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailRequestDto {
    private String toEmail;
    private String subject;
    private String content;
}

