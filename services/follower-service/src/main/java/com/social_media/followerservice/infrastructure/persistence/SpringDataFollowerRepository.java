package com.social_media.followerservice.infrastructure.persistence;

import com.social_media.followerservice.infrastructure.persistence.entity.FollowerInfraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataFollowerRepository extends JpaRepository<FollowerInfraEntity, UUID> {
    List<FollowerInfraEntity> findByFollowedUserId(UUID followedUserId);
    List<FollowerInfraEntity> findByFollowerId(UUID followerId);
    boolean existsByFollowerIdAndFollowedUserId(UUID followerId, UUID followedUserId);
    void deleteByFollowerIdAndFollowedUserId(UUID followerId, UUID followedUserId);
}
