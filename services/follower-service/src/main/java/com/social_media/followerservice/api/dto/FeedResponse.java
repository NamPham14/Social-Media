package com.social_media.followerservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedResponse {
    private UUID postId;
    private String content;
    private UUID authorId;
    private String authorName;
    private String authorAvatarUrl;
    private Instant createdAt;
}
