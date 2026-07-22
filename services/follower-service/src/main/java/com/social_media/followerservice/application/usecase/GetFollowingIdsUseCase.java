package com.social_media.followerservice.application.usecase;

import java.util.List;
import java.util.UUID;

// hiếu thêm
public interface GetFollowingIdsUseCase {
    List<UUID> execute(UUID userId);
}
