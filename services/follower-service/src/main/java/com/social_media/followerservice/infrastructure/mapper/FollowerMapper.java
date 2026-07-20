package com.social_media.followerservice.infrastructure.mapper;

import com.social_media.followerservice.domain.model.follower.aggregate.Follower;
import com.social_media.followerservice.infrastructure.persistence.entity.FollowerInfraEntity;
import org.springframework.stereotype.Component;

@Component
public class FollowerMapper {

    public Follower toDomain(FollowerInfraEntity entity) {
        if (entity == null) {
            return null;
        }

        return Follower.builder()
                .id(entity.getId())
                .followerId(entity.getFollowerId())
                .followedUserId(entity.getFollowedUserId())
                .status(entity.getStatus())
                .followedAt(entity.getFollowedAt())
                .unfollowedAt(entity.getUnfollowedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public FollowerInfraEntity toEntity(Follower domain) {
        if (domain == null) {
            return null;
        }

        return FollowerInfraEntity.builder()
                .id(domain.getId())
                .followerId(domain.getFollowerId())
                .followedUserId(domain.getFollowedUserId())
                .status(domain.getStatus())
                .followedAt(domain.getFollowedAt())
                .unfollowedAt(domain.getUnfollowedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
