package com.social_media.chatservice.domain.repository;

import com.social_media.chatservice.domain.model.aggregate.Conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository {
    Conversation save(Conversation conversation);
    Optional<Conversation> findById(Long id);
    Optional<Conversation> findByConversationId(UUID conversationId);
    List<Conversation> findByUserId(UUID userId);
    Optional<Conversation> findOneToOneBetween(UUID user1Id, UUID user2Id);
}
