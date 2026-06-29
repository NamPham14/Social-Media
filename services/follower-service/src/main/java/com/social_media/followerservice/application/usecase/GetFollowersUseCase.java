package com.social_media.followerservice.application.usecase;

import java.util.List;
import java.util.UUID;

public interface GetFollowersUseCase {
    List<UUID> getFollowers(UUID userId);
}
