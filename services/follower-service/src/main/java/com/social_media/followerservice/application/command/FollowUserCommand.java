package com.social_media.followerservice.application.command;

import com.social_media.followerservice.domain.model.UserId;

public record FollowUserCommand(UserId followerId, UserId followingId) {
}
