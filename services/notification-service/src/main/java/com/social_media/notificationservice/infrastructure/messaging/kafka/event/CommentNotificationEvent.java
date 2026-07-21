package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

import java.time.Instant;

public record CommentNotificationEvent(
        String eventId,
        String eventType,
        int version,
        Instant occurredAt,
        String commentId,
        String postId,
        String parentCommentId,
        String actorId,
        String recipientId
) {
}
