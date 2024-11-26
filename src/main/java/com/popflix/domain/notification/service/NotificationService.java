package com.popflix.domain.notification.service;

import com.popflix.domain.notification.dto.NotificationResponseDto;
import com.popflix.domain.notification.enums.NotificationType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.List;

public interface NotificationService {
    SseEmitter subscribe(Long userId);
    void sendNotification(Long userId, NotificationType type, String content);
    void sendEmailNotification(Long userId, String subject, String content);
    List<NotificationResponseDto> getUnreadNotifications(Long userId);
    void markAsRead(Long userId, Long notificationId);
}