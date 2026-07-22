package com.social_media.chatservice.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateConversationRequest(
        @NotNull UUID otherUserId
) {}
