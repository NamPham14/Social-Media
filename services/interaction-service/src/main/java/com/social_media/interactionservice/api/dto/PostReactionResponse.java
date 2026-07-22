package com.social_media.interactionservice.api.dto;

import java.util.UUID;

// Hiếu thêm
public record PostReactionResponse(UUID postId, int reactionCount) {
}
