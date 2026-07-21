package com.social_media.interactionservice.application.event;

import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ReactionNotificationEvent(
        UUID eventId,
        String eventType,
        int version,
        Instant occurredAt,
        UUID interactionId,
        TargetType targetType,
        UUID targetId,
        ReactionType reactionType,
        UUID actorId,
        UUID recipientId
) {
    public static final String REACTION_CREATED = "ReactionCreatedV1";

    public ReactionNotificationEvent {
        Objects.requireNonNull(eventId, "eventId is required");
        Objects.requireNonNull(eventType, "eventType is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
        Objects.requireNonNull(interactionId, "interactionId is required");
        Objects.requireNonNull(targetType, "targetType is required");
        Objects.requireNonNull(targetId, "targetId is required");
        Objects.requireNonNull(reactionType, "reactionType is required");
        Objects.requireNonNull(actorId, "actorId is required");
        Objects.requireNonNull(recipientId, "recipientId is required");
        if (version != 1 || !REACTION_CREATED.equals(eventType)) {
            throw new IllegalArgumentException("Unsupported reaction event contract");
        }
    }

    public static ReactionNotificationEvent from(Interaction interaction, UUID recipientId) {
        return new ReactionNotificationEvent(
                UUID.randomUUID(),
                REACTION_CREATED,
                1,
                Instant.now(),
                interaction.getId(),
                interaction.getTargetType(),
                interaction.getTargetId(),
                interaction.getReactionType(),
                interaction.getUserId(),
                recipientId
        );
    }
}
