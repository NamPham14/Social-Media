package com.social_media.followerservice.application.usecase.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.followerservice.application.dto.events.UserUnfollowedEvent;
import com.social_media.followerservice.application.command.UnfollowUserCommand;
import com.social_media.followerservice.application.usecase.UnfollowUserUseCase;
import com.social_media.followerservice.domain.model.outbox.Outbox;
import com.social_media.followerservice.domain.model.outbox.OutboxStatus;
import com.social_media.followerservice.domain.repository.FollowRelationRepository;
import com.social_media.followerservice.domain.repository.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UnfollowUserUseCaseImpl implements UnfollowUserUseCase {
    private final FollowRelationRepository followRelationRepository;
    private final OutBoxRepository outBoxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void execute(UnfollowUserCommand command) {
        followRelationRepository.deleteByFollowerIdAndFollowingId(command.followerId(), command.followingId());

        try {
            UserUnfollowedEvent event = new UserUnfollowedEvent(
                    UUID.randomUUID().toString(),
                    command.followerId().value().toString(),
                    command.followingId().value().toString()
            );

            Outbox outbox = Outbox.builder()
                    .id(UUID.randomUUID())
                    .topic("user-unfollowed-topic")
                    .eventType("USER_UNFOLLOWED")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxStatus.NEW)
                    .createdAt(LocalDateTime.now())
                    .build();

            outBoxRepository.save(outbox);
        } catch (Exception e) {
            log.error("Failed to persist outbox event for unfollow: {}", e.getMessage());
        }
    }
}
