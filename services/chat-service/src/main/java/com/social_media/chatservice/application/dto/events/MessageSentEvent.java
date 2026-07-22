package com.social_media.chatservice.application.dto.events;

import com.social_media.chatservice.domain.model.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageSentEvent(
        UUID messageId,
        UUID conversationId,
        UUID senderId,
        String content,
        MessageType type,
        Instant createdAt
) {}
