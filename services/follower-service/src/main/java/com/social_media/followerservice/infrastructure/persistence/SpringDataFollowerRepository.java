package com.social_media.followerservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataFollowerRepository extends JpaRepository<FollowerEntity, Long> {
    List<FollowerEntity> findByFollowingId(Long followingId);
    List<FollowerEntity> findByFollowerId(Long followerId);
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
