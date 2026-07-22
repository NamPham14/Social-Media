package com.social_media.chatservice.domain.model.valueobject;

import java.time.Instant;
import java.util.UUID;

public record ConversationParticipant(
        UUID userId,
        Instant lastReadAt,
        Instant leftAt
) {
    public static ConversationParticipant create(UUID userId) {
        return new ConversationParticipant(userId, null, null);
    }

    public ConversationParticipant markAsRead(Instant readAt) {
        return new ConversationParticipant(userId, readAt, leftAt);
    }

    public boolean hasLeft() {
        return leftAt != null;
    }
}
