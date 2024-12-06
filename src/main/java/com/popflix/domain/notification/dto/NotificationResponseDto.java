package com.popflix.domain.notification.dto;

import com.popflix.domain.notification.entity.Notification;
import com.popflix.domain.notification.enums.NotificationType;
import com.popflix.domain.user.entity.User;
import com.popflix.domain.user.entity.UserGenre;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class NotificationResponseDto {
    private Long id;                      // 알림 ID
    private Long userId;                  // 알림 받는 사용자 ID
    private Long reviewerId;              // 리뷰 작성자 ID
    private String reviewerNickname;      // 리뷰 작성자 닉네임
    private String reviewerProfileImage;  // 리뷰 작성자 프로필 이미지
    private Long targetId;                // 리뷰 또는 포토리뷰 ID
    private List<Long> userGenres;        // 사용자 선호 장르 목록
    private NotificationType type;        // 알림 타입 (리뷰/포토리뷰)
    private LocalDateTime createdAt;      // 알림 생성 시간
    private String content;               // 리뷰/포토리뷰 내용
    private String dateGroup;             // 날짜 그룹 (오늘/이전)
    private boolean isRead;               // 읽음 여부

    public static NotificationResponseDto from(Notification notification) {
        User reviewer = notification.getReviewer();
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getUserId())
                .reviewerId(reviewer.getUserId())
                .reviewerNickname(reviewer.getNickname())
                .reviewerProfileImage(reviewer.getProfileImage())
                .targetId(notification.getTargetId())
                .userGenres(notification.getUser().getUserGenres().stream()
                        .map(UserGenre::getGenreId)
                        .collect(Collectors.toList()))
                .type(notification.getType())
                .createdAt(notification.getCreateAt())
                .content(notification.getContent())
                .dateGroup(notification.getCreateAt().toLocalDate()
                        .equals(LocalDate.now()) ? "오늘 받은 알림" : "이전 알림")
                .isRead(notification.isRead())
                .build();
    }
}