package com.popflix.domain.notification.controller;

import com.popflix.domain.notification.dto.NotificationResponseDto;
import com.popflix.domain.notification.service.NotificationService;
import com.popflix.global.util.ApiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam Long userId) {
        return notificationService.subscribe(userId);
    }

    @GetMapping("/unread")
    public ApiUtil.ApiSuccess<List<NotificationResponseDto>> getUnreadNotifications(
            @RequestParam Long userId) {
        List<NotificationResponseDto> notifications =
                notificationService.getUnreadNotifications(userId);
        return ApiUtil.success(notifications);
    }

    @PatchMapping("/{notificationId}/read")
    public ApiUtil.ApiSuccess<Void> markAsRead(
            @RequestParam Long userId,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ApiUtil.success(null);
    }
}