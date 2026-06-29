package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.application.command.UnfollowUserCommand;
import com.social_media.followerservice.domain.repository.FollowerRepository;
import com.social_media.followerservice.domain.event.UserUnfollowedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnfollowUserUseCaseImpl implements UnfollowUserUseCase {

    private final FollowerRepository followerRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void unfollowUser(UnfollowUserCommand command) {
        if (!followerRepository.exists(command.followerId(), command.followingId())) {
            return;
        }

        followerRepository.delete(command.followerId(), command.followingId());

        eventPublisher.publishEvent(new UserUnfollowedEvent(command.followerId(), command.followingId()));
    }
}

