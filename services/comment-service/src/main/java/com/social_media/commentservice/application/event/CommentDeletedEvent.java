package com.social_media.commentservice.application.event;

import java.time.Instant;
import java.util.UUID;

public record CommentDeletedEvent(
        String id,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID commentId,
        UUID postId,
        UUID actorId
) {
    public static final String COMMENT_DELETED = "CommentDeletedV1";

    public static CommentDeletedEvent create(UUID commentId, UUID postId, UUID actorId) {
        return new CommentDeletedEvent(UUID.randomUUID().toString(), COMMENT_DELETED, 1,
                Instant.now(), commentId, postId, actorId);
    }
}
