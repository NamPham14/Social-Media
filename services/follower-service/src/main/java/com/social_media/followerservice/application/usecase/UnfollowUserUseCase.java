package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.application.command.UnfollowUserCommand;

public interface UnfollowUserUseCase {
    void unfollowUser(UnfollowUserCommand command);
}
