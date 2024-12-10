package com.popflix.domain.notification.service;

import com.popflix.domain.notification.dto.NotificationResponseDto;
import com.popflix.domain.notification.event.dto.ReviewCreatedEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

public interface NotificationService {
    SseEmitter subscribe(Long userId);
    void sendNotification(ReviewCreatedEvent event);
    void sendEmailNotification(Long userId, String subject, String content);
    List<NotificationResponseDto> getUnreadNotifications(Long userId);
    void markAsRead(Long userId, Long notificationId);
    List<NotificationResponseDto> getNotifications(Long userId);
}