package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

public record UserFollowedEvent(
        String eventId,
        Long followerUserId,
        Long followingUserId,
        String followerName
) {
}