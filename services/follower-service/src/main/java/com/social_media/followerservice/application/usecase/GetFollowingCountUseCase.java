package com.social_media.followerservice.application.usecase;

import java.util.UUID;

public interface GetFollowingCountUseCase {
    long execute(UUID userId);
}
