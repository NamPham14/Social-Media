package com.social_media.commentservice.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CommentResponse {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private UUID parentId;
    private String content;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
