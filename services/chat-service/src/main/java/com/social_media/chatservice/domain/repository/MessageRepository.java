package com.social_media.chatservice.domain.repository;

import com.social_media.chatservice.domain.model.aggregate.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(UUID messageId);
    Page<Message> findByConversationId(Long conversationId, Pageable pageable);
}
