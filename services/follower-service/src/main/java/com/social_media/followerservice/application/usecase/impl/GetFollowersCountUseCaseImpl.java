package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.application.usecase.GetFollowersCountUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetFollowersCountUseCaseImpl implements GetFollowersCountUseCase {

    private final FollowRelationRepository followRelationRepository;

    @Override
    public long execute(UUID userId) {
        return followRelationRepository.countByFollowingId(UserId.from(userId));
    }
}
