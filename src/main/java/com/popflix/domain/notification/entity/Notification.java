// domain.notification.entity.Notification.java
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notification(
            NotificationType type,
            NotificationChannel channel,
            String content,
            User user
    ) {
        this.type = type;
        this.channel = channel;
        this.content = content;
        this.status = NotificationStatus.PENDING;
        this.isRead = false;
        this.user = user;
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