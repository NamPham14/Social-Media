package com.social_media.postservice.infrastructure.client.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCountResponse {
    private UUID postId;
    private long commentCount;
}
