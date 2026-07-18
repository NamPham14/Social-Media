package com.social_media.followerservice.application.usecase.impl;

import com.social_media.common.exception.EntityNotFoundException;
import com.social_media.common.exception.ServiceUnavailableException;
import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.application.exception.CannotFollowSelfException;
import com.social_media.followerservice.application.exception.DuplicateFollowException;
import com.social_media.followerservice.application.port.FollowEventPublisher;
import com.social_media.followerservice.application.usecase.FollowUserUseCase;
import com.social_media.followerservice.application.dto.events.UserFollowedEvent;
import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.infrastructure.client.IdentityServiceClient;
import com.social_media.followerservice.infrastructure.client.ProfileServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowUserUseCaseImpl implements FollowUserUseCase {

    private final FollowRelationRepository followRelationRepository;
    private final FollowEventPublisher followEventPublisher;
    private final IdentityServiceClient identityServiceClient;
    private final ProfileServiceClient profileServiceClient;

    @Override
    @Transactional
    public FollowRelation execute(FollowUserCommand command) {

        if (command.followerId().equals(command.followingId())) {
            throw new CannotFollowSelfException();
        }

        if (followRelationRepository.existsByFollowerIdAndFollowingId(command.followerId(), command.followingId())) {
            throw new DuplicateFollowException();
        }

        // Verify target user exists and is ACTIVE
        UUID followingId = command.followingId().value();
        try {
            String status = identityServiceClient.getUserStatus(followingId);
            if (!"ACTIVE".equals(status)) {
                throw new EntityNotFoundException("Target user is not active or not found");
            }
        } catch (EntityNotFoundException | ServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceUnavailableException("Unable to verify user existence");
        }

        FollowRelation followRelation = FollowRelation.create(command.followerId(), command.followingId());

        FollowRelation saved = followRelationRepository.save(followRelation);

        // Get follower name from profile-service
        UUID followerId = command.followerId().value();
        String followerName = followerId.toString();
        try {
            var profileResponse = profileServiceClient.getProfile(followerId);
            if (profileResponse != null && profileResponse.getData() != null
                    && profileResponse.getData().getFullName() != null) {
                followerName = profileResponse.getData().getFullName();
            }
        } catch (Exception e) {
            // If profile service is down, use UUID as fallback
            followerName = followerId.toString();
        }

        followEventPublisher.publish(
                new UserFollowedEvent(
                        UUID.randomUUID().toString(),
                        followerId.toString(),
                        followingId.toString(),
                        followerName
                )
        );

        return saved;
    }
}
