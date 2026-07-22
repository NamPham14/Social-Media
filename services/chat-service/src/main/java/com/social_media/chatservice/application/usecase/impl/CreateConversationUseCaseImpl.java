package com.social_media.chatservice.application.usecase.impl;

import com.social_media.chatservice.application.exception.CannotCreateConversationWithSelfException;
import com.social_media.chatservice.application.exception.NotFollowingException;
import com.social_media.chatservice.application.usecase.CreateConversationUseCase;
import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.repository.ConversationRepository;
import com.social_media.chatservice.infrastructure.client.FollowerClient;
import com.social_media.chatservice.infrastructure.client.dto.FollowClientResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateConversationUseCaseImpl implements CreateConversationUseCase {
    private final ConversationRepository conversationRepository;
    private final FollowerClient followerClient;

    @Override
    @Transactional
    public Conversation execute(UUID currentUserId, UUID otherUserId) {
        if (currentUserId.equals(otherUserId)) {
            throw new CannotCreateConversationWithSelfException();
        }

        validateFollowPermission(currentUserId, otherUserId);

        return conversationRepository.findOneToOneBetween(currentUserId, otherUserId)
                .orElseGet(() -> {
                    Conversation conversation = Conversation.createOneToOne(currentUserId, otherUserId);
                    return conversationRepository.save(conversation);
                });
    }

    private void validateFollowPermission(UUID currentUserId, UUID otherUserId) {
        boolean follows = checkFollows(currentUserId, otherUserId);
        if (!follows) {
            throw new NotFollowingException(otherUserId);
        }
    }

    private boolean checkFollows(UUID followerId, UUID followingId) {
        try {
            var response = followerClient.getFollowing(followerId, 1, 10000);
            if (response == null || response.getData() == null) {
                return false;
            }
            List<FollowClientResponse> following = response.getData().getItems();
            return following.stream().anyMatch(f -> f.getFollowingId().equals(followingId));
        } catch (FeignException e) {
            if (e.status() == 404) {
                return false;
            }
            throw e;
        }
    }
}
