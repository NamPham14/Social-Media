package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record UserUnfollowedEvent(
        String eventId,
        String followerId,
        String followingId
) {
}
