package com.social_media.notificationservice.infrastructure.messaging.kafka.event;

import java.time.Instant;

public record ReactionCreatedEvent(
        String eventId,
        String eventType,
        int version,
        Instant occurredAt,
        String interactionId,
        String targetType,
        String targetId,
        String reactionType,
        String actorId,
        String recipientId
) {
}
