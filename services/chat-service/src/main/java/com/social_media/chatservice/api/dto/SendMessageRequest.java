package com.social_media.chatservice.api.dto;

import com.social_media.chatservice.domain.model.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
        @NotBlank String content,
        @NotNull MessageType type
) {}
