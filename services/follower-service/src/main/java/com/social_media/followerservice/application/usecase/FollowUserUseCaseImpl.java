package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.application.command.FollowUserCommand;
import com.social_media.followerservice.domain.model.follower.aggregate.Follower;
import com.social_media.followerservice.domain.repository.FollowerRepository;
import com.social_media.followerservice.domain.event.UserFollowedEvent;
import com.social_media.followerservice.domain.exception.BusinessException;
import com.social_media.followerservice.infrastructure.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FollowUserUseCaseImpl implements FollowUserUseCase {

    private final FollowerRepository followerRepository;
    private final UserClient userClient;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void followUser(FollowUserCommand command) {
        // 1. Validate không follow chính mình (Double check dù Command đã check)
        if (command.followerId().equals(command.followingId())) {
            throw new BusinessException("A user cannot follow themselves");
        }

        // Validate target user exists via UserClient
        // (Tạm thời COMMENT lại để bạn test độc lập Follower Service mà không cần bật User Service)
        // try {
        //     Object userProfile = userClient.getUserProfilesByIds(List.of(command.followingId()));
        //     if (userProfile == null) {
        //         throw new NotFoundException("Target user not found");
        //     }
        // } catch (Exception e) {
        //     throw new NotFoundException("Target user not found or error occurred: " + e.getMessage());
        // }

        // 2. Validate không follow trùng
        if (followerRepository.exists(command.followerId(), command.followingId())) {
            throw new BusinessException("You are already following this user");
        }

        // 3. Create and Save entity
        Follower follower = Follower.builder()
                .followerId(command.followerId())
                .followedUserId(command.followingId())
                .status("ACTIVE")
                .followedAt(LocalDateTime.now())
                .build();


        followerRepository.save(follower);

        // 4. Publish USER_FOLLOWED event
        eventPublisher.publishEvent(new UserFollowedEvent(command.followerId(), command.followingId(), LocalDateTime.now()));
    }
}

