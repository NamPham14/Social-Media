package com.social_media.followerservice.infrastructure.persistence;

import com.social_media.followerservice.domain.model.Follower;
import com.social_media.followerservice.domain.model.UserId;
import com.social_media.followerservice.domain.repository.FollowerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FollowerRepositoryImpl implements FollowerRepository {

    private final SpringDataFollowerRepository springDataFollowerRepository;

    public FollowerRepositoryImpl(SpringDataFollowerRepository springDataFollowerRepository) {
        this.springDataFollowerRepository = springDataFollowerRepository;
    }

    @Override
    public Follower save(Follower follower) {
        FollowerEntity entity = new FollowerEntity(
                follower.getFollowerId().value(),
                follower.getFollowingId().value()
        );
        FollowerEntity savedEntity = springDataFollowerRepository.save(entity);
        
        Follower savedFollower = Follower.create(
                new UserId(savedEntity.getFollowerId()),
                new UserId(savedEntity.getFollowingId())
        );
        savedFollower.setId(savedEntity.getId());
        return savedFollower;
    }

    @Override
    public boolean exists(UserId followerId, UserId followingId) {
        return springDataFollowerRepository.existsByFollowerIdAndFollowingId(
                followerId.value(), 
                followingId.value()
        );
    }

    @Override
    public List<UserId> findFollowingIdsByFollowerId(UserId followerId) {
        List<FollowerEntity> entities = springDataFollowerRepository.findByFollowerId(followerId.value());
        return entities.stream()
                .map(e -> new UserId(e.getFollowingId()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UserId followerId, UserId followingId) {
        springDataFollowerRepository.deleteByFollowerIdAndFollowingId(followerId.value(), followingId.value());
    }
}
