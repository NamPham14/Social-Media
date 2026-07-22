package com.social_media.chatservice.infrastructure.persistence.adapter;

import com.social_media.chatservice.domain.model.aggregate.Message;
import com.social_media.chatservice.domain.repository.MessageRepository;
import com.social_media.chatservice.infrastructure.persistence.mapper.MessagePersistenceMapper;
import com.social_media.chatservice.infrastructure.persistence.repository.MessageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryAdapter implements MessageRepository {
    private final MessageJpaRepository jpaRepository;
    private final MessagePersistenceMapper mapper;

    @Override
    public Message save(Message message) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(message)));
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return jpaRepository.findByMessageId(messageId)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Message> findByConversationId(Long conversationId, Pageable pageable) {
        return jpaRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable)
                .map(mapper::toDomain);
    }
}
