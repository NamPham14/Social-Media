package com.social_media.chatservice.api.dto;

import com.social_media.chatservice.domain.model.enums.MessageType;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID messageId,
        UUID conversationId,
        UUID senderId,
        String content,
        MessageType type,
        boolean deleted,
        Instant createdAt
) {}
