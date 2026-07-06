package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.application.exception.CannotFollowSelfException;
import com.social_media.followerservice.application.exception.DuplicateFollowException;
import com.social_media.followerservice.application.port.FollowEventPublisher;
import com.social_media.followerservice.application.usecase.FollowUserUseCase;
import com.social_media.followerservice.domain.event.UserFollowedEvent;
import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowUserUseCaseImpl implements FollowUserUseCase {
    private final FollowRelationRepository followRelationRepository;
    private final FollowEventPublisher followEventPublisher;

    @Override
    @Transactional
    public FollowRelation execute(FollowUserCommand command) {
        if (command.followerId().equals(command.followingId())) throw new CannotFollowSelfException();
        if (followRelationRepository.existsByFollowerIdAndFollowingId(command.followerId(), command.followingId()))
            throw new DuplicateFollowException();
        FollowRelation saved = followRelationRepository.save(FollowRelation.create(command.followerId(), command.followingId()));
        followEventPublisher.publish(new UserFollowedEvent(command.followerId().value(), command.followingId().value()));
        return saved;
    }
}
