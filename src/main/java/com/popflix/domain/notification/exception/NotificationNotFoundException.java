package com.popflix.domain.notification.exception;

import lombok.Getter;

@Getter
public class NotificationNotFoundException extends NotificationException {
    private final Long notificationId;

    public NotificationNotFoundException(Long notificationId) {
        super(String.format("알림을 찾을 수 없습니다. ID: %d", notificationId));
        this.notificationId = notificationId;
    }
}