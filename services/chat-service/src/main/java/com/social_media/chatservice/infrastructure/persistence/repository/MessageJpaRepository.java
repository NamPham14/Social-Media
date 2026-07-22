package com.social_media.chatservice.infrastructure.persistence.repository;

import com.social_media.chatservice.infrastructure.persistence.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    Optional<MessageEntity> findByMessageId(UUID messageId);
    Page<MessageEntity> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
}
