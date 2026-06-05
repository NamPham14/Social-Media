package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.application.command.FollowUserCommand;

public interface FollowUserUseCase {
    void followUser(FollowUserCommand command);
}
