package com.social_media.chatservice.application.usecase.impl;

import com.social_media.chatservice.application.exception.NotParticipantException;
import com.social_media.chatservice.application.usecase.MarkAsReadUseCase;
import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.repository.ConversationRepository;
import com.social_media.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkAsReadUseCaseImpl implements MarkAsReadUseCase {
    private final ConversationRepository conversationRepository;

    @Override
    @Transactional
    public void execute(UUID conversationId, UUID currentUserId) {
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.hasParticipant(currentUserId)) {
            throw new NotParticipantException();
        }

        conversation.markAsRead(currentUserId, Instant.now());
        conversationRepository.save(conversation);
    }
}
