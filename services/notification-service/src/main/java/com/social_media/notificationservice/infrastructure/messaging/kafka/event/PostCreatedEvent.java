package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record PostCreatedEvent(
        String eventId,
        String postId,
        String authorId,
        String authorName,
        String caption
) {
}
