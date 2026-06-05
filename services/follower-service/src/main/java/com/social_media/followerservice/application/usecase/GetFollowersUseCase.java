package com.social_media.followerservice.application.usecase;

import java.util.List;

public interface GetFollowersUseCase {
    List<Long> getFollowers(Long userId);
}
