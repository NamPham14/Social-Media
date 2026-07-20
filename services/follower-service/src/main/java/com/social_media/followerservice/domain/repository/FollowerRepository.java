package com.social_media.followerservice.domain.repository;

import com.social_media.followerservice.domain.model.follower.aggregate.Follower;


import java.util.List;
import java.util.UUID;

public interface FollowerRepository {
    Follower save(Follower follower);
    boolean exists(UUID followerId, UUID followedUserId);
    List<UUID> findFollowedUserIdsByFollowerId(UUID followerId);
    List<UUID> findFollowerIdsByFollowedUserId(UUID followedUserId);
    void delete(UUID followerId, UUID followedUserId);
}
