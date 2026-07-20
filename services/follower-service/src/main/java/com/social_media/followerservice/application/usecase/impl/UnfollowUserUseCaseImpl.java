package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.application.dto.events.UserUnfollowedEvent;
import com.social_media.followerservice.application.command.UnfollowUserCommand;
import com.social_media.followerservice.application.usecase.UnfollowUserUseCase;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.infrastructure.messaging.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnfollowUserUseCaseImpl implements UnfollowUserUseCase {
    private final FollowRelationRepository followRelationRepository;
    private final KafkaEventProducer eventProducer;

    @Override
    @Transactional
    public void execute(UnfollowUserCommand command) {
        followRelationRepository.deleteByFollowerIdAndFollowingId(command.followerId(), command.followingId());

        eventProducer.publishUnfollowed(new UserUnfollowedEvent(
                UUID.randomUUID().toString(),
                command.followerId().value().toString(),
                command.followingId().value().toString()
        ));
    }
}
