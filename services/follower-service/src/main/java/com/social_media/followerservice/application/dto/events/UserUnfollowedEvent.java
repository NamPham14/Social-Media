package com.social_media.followerservice.application.dto.events;

public record UserUnfollowedEvent(
        String eventId,
        String followerId,
        String followingId
) {
}
