package com.social_media.followerservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserFollowedEvent(UUID followerId, UUID followingId, LocalDateTime createdAt) {
}
