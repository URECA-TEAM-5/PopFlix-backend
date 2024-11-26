package com.popflix.domain.notification.enums;

public enum NotificationType {
    NEW_REVIEW("새 리뷰 알림"),
    NEW_PHOTO_REVIEW("새 포토리뷰 알림");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}