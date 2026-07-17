package com.social_media.followerservice.domain.event;

import java.util.UUID;

public record UserFollowedEvent(UUID followerId, UUID followingId) {}
