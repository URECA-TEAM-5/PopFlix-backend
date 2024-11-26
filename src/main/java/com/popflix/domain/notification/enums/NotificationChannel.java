package com.popflix.domain.notification.enums;

public enum NotificationChannel {
    SSE("실시간 알림"),
    EMAIL("이메일 알림");

    private final String description;

    NotificationChannel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
