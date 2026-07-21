package com.social_media.commentservice.infrastructure.messaging.outbox;

import java.util.UUID;

public record CommentOutboxMessage(
        UUID eventId,
        UUID aggregateId,
        String eventType,
        String topic,
        String payload,
        int attempts
) {
}
