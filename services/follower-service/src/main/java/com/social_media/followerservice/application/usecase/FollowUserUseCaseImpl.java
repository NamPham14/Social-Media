package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.domain.event.UserFollowedEvent;
import com.social_media.followerservice.domain.exception.NotFoundException;
import com.social_media.followerservice.domain.model.Follower;
import com.social_media.followerservice.domain.repository.FollowerRepository;
import com.social_media.followerservice.infrastructure.client.UserClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowUserUseCaseImpl implements FollowUserUseCase {

    private final FollowerRepository followerRepository;
    private final UserClient userClient;
    private final ApplicationEventPublisher eventPublisher;

    public FollowUserUseCaseImpl(FollowerRepository followerRepository, 
                                 UserClient userClient, 
                                 ApplicationEventPublisher eventPublisher) {
        this.followerRepository = followerRepository;
        this.userClient = userClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void followUser(FollowUserCommand command) {
        // a. Check if the target user exists
        try {
            Object userProfile = userClient.getUserProfilesByIds(List.of(command.followingId().value()));
            if (userProfile == null) {
                throw new NotFoundException("Target user not found");
            }
        } catch (Exception e) {
            throw new NotFoundException("Target user not found or error occurred: " + e.getMessage());
        }

        // b. Check if the follow relation already exists
        if (followerRepository.exists(command.followerId(), command.followingId())) {
            return;
        }

        // c. Create domain entity
        Follower follower = Follower.create(command.followerId(), command.followingId());

        // d. Save via followerRepository
        followerRepository.save(follower);

        // e. Publish an internal Spring event
        eventPublisher.publishEvent(new UserFollowedEvent(command.followerId().value(), command.followingId().value()));
    }
}
