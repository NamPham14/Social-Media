package com.social_media.followerservice.domain.event;

import java.util.UUID;

public record UserUnfollowedEvent(UUID followerId, UUID followingId) {
}
