package com.social_media.chatservice.application.usecase;

import com.social_media.chatservice.domain.model.aggregate.Conversation;

import java.util.UUID;

public interface GetConversationDetailUseCase {
    Conversation execute(UUID conversationId, UUID currentUserId);
}
