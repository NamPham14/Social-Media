package com.social_media.chatservice.infrastructure.persistence.repository;

import com.social_media.chatservice.infrastructure.persistence.entity.ConversationParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationParticipantJpaRepository extends JpaRepository<ConversationParticipantEntity, Long> {
    Optional<ConversationParticipantEntity> findByConversationIdAndUserId(Long conversationId, UUID userId);
}
