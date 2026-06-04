package com.social_media.followerservice.domain.repository;

import com.social_media.followerservice.domain.model.Follower;
import com.social_media.followerservice.domain.model.UserId;
import java.util.List;

public interface FollowerRepository {
    Follower save(Follower follower);
    boolean exists(UserId followerId, UserId followingId);
    List<UserId> findFollowingIdsByFollowerId(UserId followerId);
    void delete(UserId followerId, UserId followingId);
}
