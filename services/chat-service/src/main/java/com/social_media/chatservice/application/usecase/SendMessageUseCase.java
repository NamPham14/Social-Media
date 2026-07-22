package com.social_media.chatservice.application.usecase;

import com.social_media.chatservice.domain.model.aggregate.Message;
import com.social_media.chatservice.domain.model.enums.MessageType;

import java.util.UUID;

public interface SendMessageUseCase {
    Message execute(UUID conversationId, UUID senderId, String content, MessageType type);
}
