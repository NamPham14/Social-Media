package com.social_media.followerservice.api.dto;

import java.time.Instant;

public record FeedResponse(
        Long postId,
        String content,
        Long authorId,
        String authorName,
        String authorAvatarUrl,
        Instant createdAt
) {
}
