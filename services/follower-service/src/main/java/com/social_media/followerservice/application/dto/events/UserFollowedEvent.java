package com.social_media.followerservice.application.dto.events;

public record UserFollowedEvent(
        String eventId,
        String followerId,
        String followingId,
        String followerName
) {
}
