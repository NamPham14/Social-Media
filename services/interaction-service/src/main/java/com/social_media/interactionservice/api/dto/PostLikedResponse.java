package com.social_media.interactionservice.api.dto;

import java.util.UUID;

// Hiếu thêm
public record PostLikedResponse(UUID postId, boolean likedByMe) {
}
