package com.popflix.domain.notification.service.impl;

import com.popflix.domain.notification.dto.EmailRequestDto;
import com.popflix.domain.notification.exception.NotificationException;
import com.popflix.domain.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendEmail(EmailRequestDto emailRequest) {
        sendEmail(emailRequest.getToEmail(),
                emailRequest.getSubject(),
                emailRequest.getContent());
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new NotificationException("Failed to send email", e);
        }
    }
}