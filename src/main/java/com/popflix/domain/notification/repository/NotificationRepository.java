package com.popflix.domain.notification.repository;

import com.popflix.domain.notification.entity.Notification;
import com.popflix.domain.notification.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n " +
            "WHERE n.id = :notificationId " +
            "AND n.isDeleted = false")
    Optional<Notification> findActiveById(@Param("notificationId") Long notificationId);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.user.userId = :userId " +
            "AND n.isRead = false " +
            "AND n.isDeleted = false " +
            "ORDER BY n.createAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM Notification n " +
            "WHERE n.status = :status " +
            "AND n.isDeleted = false " +
            "ORDER BY n.createAt ASC")
    List<Notification> findByStatus(@Param("status") NotificationStatus status);
}
