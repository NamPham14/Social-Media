package com.social_media.commentservice.api.dto;

import java.util.UUID;

public record CommentCountResponse(UUID postId, long commentCount) {
}
