package com.social_media.followerservice.domain;

import com.social_media.followerservice.domain.Follower;
import com.social_media.followerservice.domain.UserId;
import java.util.List;

public interface FollowerRepository {
    Follower save(Follower follower);
    boolean exists(UserId followerId, UserId followingId);
    List<UserId> findFollowingIdsByFollowerId(UserId followerId);
    void delete(UserId followerId, UserId followingId);
}
