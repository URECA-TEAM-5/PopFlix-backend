package com.popflix.domain.notification.dto;

import com.popflix.domain.notification.entity.Notification;
import com.popflix.domain.notification.enums.NotificationChannel;
import com.popflix.domain.notification.enums.NotificationStatus;
import com.popflix.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {
    private Long id;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .status(notification.getStatus())
                .content(notification.getContent())
                .isRead(notification.isRead())
                .createdAt(notification.getCreateAt())
                .build();
    }
}
