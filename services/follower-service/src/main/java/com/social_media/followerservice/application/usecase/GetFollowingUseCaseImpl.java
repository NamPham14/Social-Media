package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.domain.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetFollowingUseCaseImpl implements GetFollowingUseCase {

    private final FollowerRepository followerRepository;

    @Override
    public List<UUID> getFollowing(UUID userId) {
        return followerRepository.findFollowedUserIdsByFollowerId(userId);
    }
}
