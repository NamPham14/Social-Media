package com.social_media.followerservice.application.usecase;

import java.util.List;
import java.util.UUID;

public interface GetFollowingUseCase {
    List<UUID> getFollowing(UUID userId);
}
