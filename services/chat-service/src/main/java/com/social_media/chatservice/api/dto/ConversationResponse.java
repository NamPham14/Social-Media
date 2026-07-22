package com.social_media.chatservice.api.dto;

import com.social_media.chatservice.domain.model.enums.ConversationType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationResponse(
        UUID conversationId,
        ConversationType type,
        List<UUID> participantIds,
        MessageResponse lastMessage,
        Instant createdAt,
        Instant updatedAt,
        Instant lastReadAt
) {}
