package com.social_media.followerservice.application.usecase;

import java.util.List;

public interface GetFollowingUseCase {
    List<Long> getFollowing(Long userId);
}
