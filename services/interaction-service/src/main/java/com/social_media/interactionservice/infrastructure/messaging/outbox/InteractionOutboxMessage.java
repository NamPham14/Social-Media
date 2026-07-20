package com.social_media.interactionservice.infrastructure.messaging.outbox;

import java.util.UUID;

public record InteractionOutboxMessage(
        UUID eventId,
        UUID aggregateId,
        String eventType,
        String topic,
        String payload,
        int attempts
) {
}
