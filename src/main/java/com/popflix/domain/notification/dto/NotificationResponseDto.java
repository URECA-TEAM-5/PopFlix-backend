package com.popflix.domain.notification.dto;

import com.popflix.domain.notification.entity.Notification;
import com.popflix.domain.notification.enums.NotificationChannel;
import com.popflix.domain.notification.enums.NotificationStatus;
import com.popflix.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {
    private Long id;
    private NotificationType type;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String dateGroup;

    // Entity -> DTO 변환 메서드
    public static NotificationResponseDto from(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .content(notification.getContent())
                .createdAt(notification.getCreateAt())
                .isRead(notification.isRead())
                .dateGroup(notification.getCreateAt().toLocalDate().equals(LocalDate.now()) ?
                        "오늘 받은 알림" : "이전 알림")
                .build();
    }
}
