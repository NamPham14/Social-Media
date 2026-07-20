package com.social_media.notificationservice.api.dto.response;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String actorId,
        String actorName,
        String actorAvatarUrl,
        String notificationType,
        String targetType,
        String targetId,
        String message,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
}
