package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record PostCreatedEvent(
        String eventId,
        Long postId,
        Long authorId,
        String authorName,
        String caption
) {
}