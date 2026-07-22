package com.social_media.chatservice.application.usecase;

import java.util.UUID;

public interface DeleteMessageUseCase {
    void execute(UUID messageId, UUID currentUserId);
}
