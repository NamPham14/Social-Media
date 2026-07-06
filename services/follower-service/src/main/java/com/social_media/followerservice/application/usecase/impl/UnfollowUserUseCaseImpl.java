package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.application.command.UnfollowUserCommand;
import com.social_media.followerservice.application.usecase.UnfollowUserUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnfollowUserUseCaseImpl implements UnfollowUserUseCase {
    private final FollowRelationRepository followRelationRepository;

    @Override
    @Transactional
    public void execute(UnfollowUserCommand command) {
        followRelationRepository.deleteByFollowerIdAndFollowingId(command.followerId(), command.followingId());
    }
}
