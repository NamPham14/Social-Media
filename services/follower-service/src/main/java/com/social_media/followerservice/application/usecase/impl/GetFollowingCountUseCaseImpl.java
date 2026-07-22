package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.application.usecase.GetFollowingCountUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetFollowingCountUseCaseImpl implements GetFollowingCountUseCase {

    private final FollowRelationRepository followRelationRepository;

    @Override
    public long execute(UUID userId) {
        return followRelationRepository.countByFollowerId(UserId.from(userId));
    }
}
