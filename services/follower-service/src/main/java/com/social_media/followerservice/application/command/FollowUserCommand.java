package com.social_media.followerservice.application.command;
import com.social_media.followerservice.domain.shared.valueobject.UserId;

public record FollowUserCommand(UserId followerId, UserId followingId) {}
