package com.popflix.domain.notification.entity;

import com.popflix.common.entity.BaseSoftDeleteEntity;
import com.popflix.domain.notification.enums.NotificationChannel;
import com.popflix.domain.notification.enums.NotificationStatus;
import com.popflix.domain.notification.enums.NotificationType;
import com.popflix.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseSoftDeleteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;                // 알림을 받는 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;            // 리뷰 작성자

    @Column(name = "target_id")
    private Long targetId;            // 리뷰 또는 포토리뷰 ID

    @Column(length = 1000, nullable = false)
    private String content;           // 알림 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;    // 알림 타입 (리뷰/포토리뷰)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private boolean isRead;           // 읽음 여부

    @Builder
    public Notification(
            User user,
            User reviewer,
            Long targetId,
            String content,
            NotificationType type,
            NotificationChannel channel) {
        this.user = user;
        this.reviewer = reviewer;
        this.targetId = targetId;
        this.content = content;
        this.type = type;
        this.channel = channel;
        this.status = NotificationStatus.PENDING;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
    }

    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }
}