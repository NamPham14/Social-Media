package com.social_media.chatservice.application.usecase;

import java.util.UUID;

public interface MarkAsReadUseCase {
    void execute(UUID conversationId, UUID currentUserId);
}
