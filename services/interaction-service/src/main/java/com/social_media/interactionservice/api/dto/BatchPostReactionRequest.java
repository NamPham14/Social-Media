package com.social_media.interactionservice.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

// Hiếu thêm
public record BatchPostReactionRequest(
        @NotEmpty @Size(max = 100) List<@Valid UUID> postIds) {
}
