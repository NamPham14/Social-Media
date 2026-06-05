package com.social_media.followerservice.infrastructure.persistence;

import com.social_media.followerservice.domain.Follower;
import com.social_media.followerservice.domain.FollowerRepository;
import com.social_media.followerservice.domain.UserId;
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
        return springDataFollowerRepository.save(follower);
    }

    @Override
    public boolean exists(UserId followerId, UserId followingId) {
        return springDataFollowerRepository.existsByFollowerIdValueAndFollowingIdValue(
                followerId.value(), 
                followingId.value()
        );
    }

    @Override
    public List<UserId> findFollowingIdsByFollowerId(UserId followerId) {
        List<Follower> followers = springDataFollowerRepository.findByFollowerIdValue(followerId.value());
        return followers.stream()
                .map(Follower::getFollowingId)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UserId followerId, UserId followingId) {
        springDataFollowerRepository.deleteByFollowerIdValueAndFollowingIdValue(followerId.value(), followingId.value());
    }
}
