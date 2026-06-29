package com.social_media.followerservice.application.command;

import java.util.UUID;

public record UnfollowUserCommand(UUID followerId, UUID followingId) {
    public UnfollowUserCommand {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("followerId and followingId cannot be null");
        }
    }
}
