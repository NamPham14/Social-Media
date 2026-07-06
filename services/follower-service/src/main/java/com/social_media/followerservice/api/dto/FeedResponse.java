package com.social_media.followerservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedResponse {
    private Long postId;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorAvatarUrl;
    private Instant createdAt;
}
