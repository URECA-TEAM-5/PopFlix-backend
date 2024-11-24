package com.popflix.domain.notification.service;

import com.popflix.domain.notification.dto.EmailRequestDto;

public interface EmailService {
    void sendEmail(EmailRequestDto emailRequest);
    void sendEmail(String to, String subject, String content);
}
