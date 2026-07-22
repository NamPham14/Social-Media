package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.application.usecase.GetFollowingIdsUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// hiếu thêm
@Service
@RequiredArgsConstructor
public class GetFollowingIdsUseCaseImpl implements GetFollowingIdsUseCase {

    private final FollowRelationRepository followRelationRepository;

    @Override
    public List<UUID> execute(UUID userId) {
        return followRelationRepository.findFollowingIdsByFollowerId(UserId.from(userId));
    }
}
