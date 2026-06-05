package com.social_media.followerservice.application.command;

import com.social_media.followerservice.domain.UserId;

public record FollowUserCommand(UserId followerId, UserId followingId) {
}
