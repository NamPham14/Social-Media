package com.social_media.followerservice.application.usecase;

import java.util.UUID;

public interface GetFollowersCountUseCase {
    long execute(UUID userId);
}
