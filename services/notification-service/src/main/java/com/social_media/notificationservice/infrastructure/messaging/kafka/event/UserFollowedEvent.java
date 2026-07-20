package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record UserFollowedEvent(
        String eventId,
        String followerId,
        String followingId,
        String followerName
) {
}
