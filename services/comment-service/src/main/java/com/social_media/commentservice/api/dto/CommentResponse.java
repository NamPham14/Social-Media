package com.social_media.commentservice.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class CommentResponse {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private String authorName;
    private  String authorAvatarUrl;
    private UUID parentId;
    private long replyCount;
    private int reactionCount;
    private boolean likedByMe;
    private String content;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
