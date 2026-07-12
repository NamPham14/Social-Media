package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record PostLikedEvent(
        String eventId,
        Long postId,
        Long postOwnerId,
        Long actorId,
        String actorName
) {
}