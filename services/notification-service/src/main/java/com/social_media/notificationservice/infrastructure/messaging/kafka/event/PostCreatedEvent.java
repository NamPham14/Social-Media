package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

import java.time.LocalDateTime;

public record PostCreatedEvent(
        String id,
        String postId,
        String authorId,
        String caption,
        LocalDateTime createdAt
) {
}
