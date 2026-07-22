package com.social_media.chatservice.utils.mapper;

import com.social_media.chatservice.api.dto.ConversationResponse;
import com.social_media.chatservice.api.dto.MessageResponse;
import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.model.aggregate.Message;
import com.social_media.chatservice.domain.model.valueobject.ConversationParticipant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChatApiMapper {

    @Mapping(target = "participantIds", source = "participants", qualifiedByName = "toUserIds")
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "lastReadAt", ignore = true)
    ConversationResponse toConversationResponse(Conversation conversation);

    default ConversationResponse toConversationResponse(Conversation conversation, Message lastMessage, UUID currentUserId) {
        MessageResponse lastMsg = lastMessage != null ? toMessageResponse(lastMessage, conversation.getConversationId()) : null;
        ConversationParticipant participant = conversation.getParticipant(currentUserId);
        return new ConversationResponse(
                conversation.getConversationId(),
                conversation.getType(),
                conversation.getParticipants().stream().map(ConversationParticipant::userId).toList(),
                lastMsg,
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                participant != null ? participant.lastReadAt() : null
        );
    }

    @Mapping(target = "conversationId", ignore = true)
    @Mapping(target = "deleted", source = "deletedForSender")
    MessageResponse toMessageResponse(Message message);

    default MessageResponse toMessageResponse(Message message, UUID conversationId) {
        MessageResponse basic = toMessageResponse(message);
        return new MessageResponse(
                basic.messageId(),
                conversationId,
                basic.senderId(),
                basic.content(),
                basic.type(),
                basic.deleted(),
                basic.createdAt()
        );
    }

    @Named("toUserIds")
    default List<UUID> toUserIds(List<ConversationParticipant> participants) {
        return participants.stream().map(ConversationParticipant::userId).toList();
    }
}
