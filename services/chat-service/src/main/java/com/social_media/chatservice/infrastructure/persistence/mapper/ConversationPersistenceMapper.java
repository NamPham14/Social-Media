package com.social_media.chatservice.infrastructure.persistence.mapper;

import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.model.valueobject.ConversationParticipant;
import com.social_media.chatservice.infrastructure.persistence.entity.ConversationEntity;
import com.social_media.chatservice.infrastructure.persistence.entity.ConversationParticipantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConversationPersistenceMapper {

    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ConversationEntity toEntity(Conversation conversation);

    default ConversationEntity toEntity(Conversation conversation, List<ConversationParticipantEntity> participantEntities) {
        ConversationEntity entity = new ConversationEntity();
        entity.setId(conversation.getId());
        entity.setConversationId(conversation.getConversationId());
        entity.setType(conversation.getType());
        entity.setConversationKey(conversation.getConversationKey());
        entity.setParticipants(participantEntities);
        participantEntities.forEach(p -> p.setConversation(entity));
        return entity;
    }

    @Mapping(target = "participants", ignore = true)
    Conversation toDomain(ConversationEntity entity);

    default Conversation toDomain(ConversationEntity entity, List<ConversationParticipant> participants) {
        return new Conversation(
                entity.getId(),
                entity.getConversationId(),
                entity.getType(),
                entity.getConversationKey(),
                participants,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    ConversationParticipantEntity toParticipantEntity(ConversationParticipant participant);

    ConversationParticipant toParticipantDomain(ConversationParticipantEntity entity);
}
