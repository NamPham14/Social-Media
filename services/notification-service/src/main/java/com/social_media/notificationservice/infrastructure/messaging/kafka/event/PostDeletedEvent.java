package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record PostDeletedEvent(
        String id,
        String postId,
        String authorId
) {
}
