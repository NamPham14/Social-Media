package com.social_media.chatservice.application.usecase;

import com.social_media.chatservice.domain.model.aggregate.Conversation;

import java.util.List;
import java.util.UUID;

public interface GetConversationsUseCase {
    List<Conversation> execute(UUID userId);
}
