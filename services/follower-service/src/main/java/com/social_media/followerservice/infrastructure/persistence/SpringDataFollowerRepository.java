package com.social_media.followerservice.infrastructure.persistence;

import com.social_media.followerservice.domain.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataFollowerRepository extends JpaRepository<Follower, Long> {
    List<Follower> findByFollowingIdValue(Long followingId);
    List<Follower> findByFollowerIdValue(Long followerId);
    boolean existsByFollowerIdValueAndFollowingIdValue(Long followerId, Long followingId);
    void deleteByFollowerIdValueAndFollowingIdValue(Long followerId, Long followingId);
}
