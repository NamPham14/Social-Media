package com.social_media.chatservice.infrastructure.persistence.mapper;

import com.social_media.chatservice.domain.model.aggregate.Message;
import com.social_media.chatservice.infrastructure.persistence.entity.MessageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessagePersistenceMapper {

    MessageEntity toEntity(Message message);

    Message toDomain(MessageEntity entity);
}
