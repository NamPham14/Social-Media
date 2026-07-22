package com.social_media.chatservice.application.usecase;

import com.social_media.chatservice.domain.model.aggregate.Conversation;

import java.util.UUID;

public interface CreateConversationUseCase {
    Conversation execute(UUID currentUserId, UUID otherUserId);
}
