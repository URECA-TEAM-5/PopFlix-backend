package com.popflix.domain.notification.enums;

public enum NotificationStatus {
    PENDING("대기"),
    SENT("전송완료"),
    FAILED("전송실패");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}