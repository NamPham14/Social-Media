package com.social_media.commentservice.application.event;

import com.social_media.commentservice.domain.model.Comment;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CommentNotificationEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID commentId,
        UUID postId,
        UUID parentCommentId,
        UUID actorId,
        UUID recipientId
) {
    public static final String COMMENT_CREATED = "CommentCreatedV1";
    public static final String COMMENT_REPLIED = "CommentRepliedV1";

    public CommentNotificationEvent {
        Objects.requireNonNull(eventId, "eventId is required");
        Objects.requireNonNull(eventType, "eventType is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
        Objects.requireNonNull(commentId, "commentId is required");
        Objects.requireNonNull(postId, "postId is required");
        Objects.requireNonNull(actorId, "actorId is required");
        Objects.requireNonNull(recipientId, "recipientId is required");
        if (version != 1) {
            throw new IllegalArgumentException("Only event version 1 is supported");
        }
        if (!COMMENT_CREATED.equals(eventType) && !COMMENT_REPLIED.equals(eventType)) {
            throw new IllegalArgumentException("Unsupported comment event type: " + eventType);
        }
        if (COMMENT_CREATED.equals(eventType) && parentCommentId != null) {
            throw new IllegalArgumentException("CommentCreatedV1 cannot have a parentCommentId");
        }
        if (COMMENT_REPLIED.equals(eventType) && parentCommentId == null) {
            throw new IllegalArgumentException("CommentRepliedV1 requires parentCommentId");
        }
    }

    public static CommentNotificationEvent from(Comment comment, UUID recipientId) {
        return new CommentNotificationEvent(
                UUID.randomUUID(),
                comment.getParentId() == null ? COMMENT_CREATED : COMMENT_REPLIED,
                1,
                Instant.now(),
                comment.getId(),
                comment.getPostId(),
                comment.getParentId(),
                comment.getUserId(),
                recipientId
        );
    }
}
