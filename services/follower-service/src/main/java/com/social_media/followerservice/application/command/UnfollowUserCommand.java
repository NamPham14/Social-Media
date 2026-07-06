package com.social_media.followerservice.application.command;
import com.social_media.followerservice.domain.shared.valueobject.UserId;

public record UnfollowUserCommand(UserId followerId, UserId followingId) {}
