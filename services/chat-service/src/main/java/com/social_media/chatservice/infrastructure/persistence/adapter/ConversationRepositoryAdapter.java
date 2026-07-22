package com.social_media.chatservice.infrastructure.persistence.adapter;

import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.model.valueobject.ConversationParticipant;
import com.social_media.chatservice.domain.repository.ConversationRepository;
import com.social_media.chatservice.infrastructure.persistence.entity.ConversationEntity;
import com.social_media.chatservice.infrastructure.persistence.entity.ConversationParticipantEntity;
import com.social_media.chatservice.infrastructure.persistence.mapper.ConversationPersistenceMapper;
import com.social_media.chatservice.infrastructure.persistence.repository.ConversationJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConversationRepositoryAdapter implements ConversationRepository {
    private final ConversationJpaRepository jpaRepository;
    private final ConversationPersistenceMapper mapper;

    @Override
    @Transactional
    public Conversation save(Conversation conversation) {
        if (conversation.getId() != null) {
            ConversationEntity existing = jpaRepository.findById(conversation.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));
            for (ConversationParticipant participant : conversation.getParticipants()) {
                ConversationParticipantEntity pe = existing.getParticipants().stream()
                        .filter(p -> p.getUserId().equals(participant.userId()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
                pe.setLastReadAt(participant.lastReadAt());
                pe.setLeftAt(participant.leftAt());
            }
            ConversationEntity saved = jpaRepository.save(existing);
            return toDomainWithParticipants(saved);
        } else {
            List<ConversationParticipantEntity> participantEntities = conversation.getParticipants().stream()
                    .map(mapper::toParticipantEntity)
                    .toList();
            ConversationEntity entity = mapper.toEntity(conversation, participantEntities);
            ConversationEntity saved = jpaRepository.save(entity);
            return toDomainWithParticipants(saved);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findById(Long id) {
        return jpaRepository.findById(id)
                .map(this::toDomainWithParticipants);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findByConversationId(UUID conversationId) {
        return jpaRepository.findByConversationId(conversationId)
                .map(this::toDomainWithParticipants);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conversation> findByUserId(UUID userId) {
        return jpaRepository.findByParticipantUserId(userId).stream()
                .map(this::toDomainWithParticipants)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findOneToOneBetween(UUID user1Id, UUID user2Id) {
        String key = Conversation.generateOneToOneKey(user1Id, user2Id);
        return jpaRepository.findByConversationKey(key)
                .map(this::toDomainWithParticipants);
    }

    private Conversation toDomainWithParticipants(ConversationEntity entity) {
        List<ConversationParticipant> participants = entity.getParticipants().stream()
                .map(mapper::toParticipantDomain)
                .toList();
        return mapper.toDomain(entity, participants);
    }
}
