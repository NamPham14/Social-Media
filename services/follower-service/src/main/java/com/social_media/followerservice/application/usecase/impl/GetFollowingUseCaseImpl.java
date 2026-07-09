package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.api.dto.FollowResponse;
import com.social_media.followerservice.application.mapper.FollowerApiMapper;
import com.social_media.followerservice.application.usecase.GetFollowingUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.shared.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetFollowingUseCaseImpl implements GetFollowingUseCase {

    private final FollowRelationRepository followRelationRepository;
    private final FollowerApiMapper followerApiMapper;

    @Override
    public Page<FollowResponse> execute(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return followRelationRepository.findByFollowerId(UserId.from(userId), pageable)
                .map(followerApiMapper::toResponse);
    }
}
