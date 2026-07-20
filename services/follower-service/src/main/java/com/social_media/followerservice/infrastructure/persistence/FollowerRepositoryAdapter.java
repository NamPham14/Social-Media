package com.social_media.followerservice.infrastructure.persistence;

import com.social_media.followerservice.domain.model.follower.aggregate.Follower;
import com.social_media.followerservice.domain.repository.FollowerRepository;
import com.social_media.followerservice.infrastructure.mapper.FollowerMapper;
import com.social_media.followerservice.infrastructure.persistence.entity.FollowerInfraEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FollowerRepositoryAdapter implements FollowerRepository {

    private final SpringDataFollowerRepository springDataFollowerRepository;
    private final FollowerMapper followerMapper;

    @Override
    public Follower save(Follower follower) {
        FollowerInfraEntity entity = followerMapper.toEntity(follower);
        FollowerInfraEntity savedEntity = springDataFollowerRepository.save(entity);
        return followerMapper.toDomain(savedEntity);
    }

    @Override
    public boolean exists(UUID followerId, UUID followedUserId) {
        return springDataFollowerRepository.existsByFollowerIdAndFollowedUserId(followerId, followedUserId);
    }

    @Override
    public List<UUID> findFollowedUserIdsByFollowerId(UUID followerId) {
        return springDataFollowerRepository.findByFollowerId(followerId).stream()
                .map(FollowerInfraEntity::getFollowedUserId)
                .collect(Collectors.toList());
    }

    @Override
    public List<UUID> findFollowerIdsByFollowedUserId(UUID followedUserId) {
        return springDataFollowerRepository.findByFollowedUserId(followedUserId).stream()
                .map(FollowerInfraEntity::getFollowerId)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID followerId, UUID followedUserId) {
        springDataFollowerRepository.deleteByFollowerIdAndFollowedUserId(followerId, followedUserId);
    }
}
