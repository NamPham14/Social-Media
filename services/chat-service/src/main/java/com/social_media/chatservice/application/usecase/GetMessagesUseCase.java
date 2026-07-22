package com.social_media.chatservice.application.usecase;

import com.social_media.chatservice.domain.model.aggregate.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetMessagesUseCase {
    Page<Message> execute(UUID conversationId, UUID currentUserId, Pageable pageable);
}
