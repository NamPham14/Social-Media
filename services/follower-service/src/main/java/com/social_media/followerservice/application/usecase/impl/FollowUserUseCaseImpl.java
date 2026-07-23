package com.social_media.followerservice.application.usecase.impl;

import com.social_media.common.exception.EntityNotFoundException;
import com.social_media.common.exception.ServiceUnavailableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.application.exception.CannotFollowSelfException;
import com.social_media.followerservice.application.exception.DuplicateFollowException;
import com.social_media.followerservice.application.usecase.FollowUserUseCase;
import com.social_media.followerservice.application.dto.events.UserFollowedEvent;
import com.social_media.followerservice.domain.model.follow.aggregate.FollowRelation;
import com.social_media.followerservice.domain.model.outbox.Outbox;
import com.social_media.followerservice.domain.model.outbox.OutboxStatus;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.repository.OutBoxRepository;
import com.social_media.followerservice.infrastructure.client.IdentityServiceClient;
import com.social_media.followerservice.infrastructure.client.ProfileServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowUserUseCaseImpl implements FollowUserUseCase {

    private final FollowRelationRepository followRelationRepository;
    private final IdentityServiceClient identityServiceClient;
    private final ProfileServiceClient profileServiceClient;
    private final OutBoxRepository outBoxRepository;
    private final ObjectMapper objectMapper;

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
            String status = identityServiceClient.getUserStatus(followingId); //
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

        try {
            UserFollowedEvent event = new UserFollowedEvent(
                    UUID.randomUUID().toString(),
                    followerId.toString(),
                    followingId.toString(),
                    followerName
            );

            Outbox outbox = Outbox.builder()
                    .id(UUID.randomUUID())
                    .topic("user-followed-topic")
                    .eventType("USER_FOLLOWED")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .build();

            outBoxRepository.save(outbox);
        } catch (Exception e) {
            log.error("Failed to persist outbox event for follow: {}", e.getMessage());
        }

        return saved;
    }
}
