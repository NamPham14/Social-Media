package com.social_media.notificationservice.infrastructure.client.post.dto;

import java.time.LocalDateTime;

public record PostClientResponse(
        Long id,
        Long userId,
        String caption,
        String locationName,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}