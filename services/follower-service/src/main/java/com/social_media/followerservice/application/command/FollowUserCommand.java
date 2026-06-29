package com.social_media.followerservice.application.command;

import java.util.UUID;

public record FollowUserCommand(UUID followerId, UUID followingId) {
    public FollowUserCommand {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("followerId and followingId cannot be null");
        }
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("A user cannot follow themselves");
        }
    }
}
