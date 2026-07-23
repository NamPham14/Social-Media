package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.application.usecase.GetFollowerIdsUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetFollowerIdsUseCaseImpl implements GetFollowerIdsUseCase {
    private final FollowRelationRepository followRelationRepository;

    @Override
    public Page<UUID> execute(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return followRelationRepository.findFollowerIdsByFollowingId(UserId.from(userId), pageable);
    }
}
