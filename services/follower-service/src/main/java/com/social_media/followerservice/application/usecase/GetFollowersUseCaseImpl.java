package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.domain.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetFollowersUseCaseImpl implements GetFollowersUseCase {

    private final FollowerRepository followerRepository;

    @Override
    public List<UUID> getFollowers(UUID userId) {
        return followerRepository.findFollowerIdsByFollowedUserId(userId);
    }
}
