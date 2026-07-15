package com.social_media.notificationservice.api.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long actorId,
        String notificationType,
        String targetType,
        Long targetId,
        String message,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
}
