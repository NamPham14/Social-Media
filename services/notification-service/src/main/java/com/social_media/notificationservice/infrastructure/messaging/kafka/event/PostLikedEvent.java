package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record PostLikedEvent(
        String eventId,
        String postId,
        String postOwnerId,
        String actorId,
        String actorName
) {
}
