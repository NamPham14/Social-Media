package com.social_media.chatservice.application.usecase.impl;

import com.social_media.chatservice.application.exception.NotParticipantException;
import com.social_media.chatservice.application.usecase.GetConversationDetailUseCase;
import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.repository.ConversationRepository;
import com.social_media.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetConversationDetailUseCaseImpl implements GetConversationDetailUseCase {
    private final ConversationRepository conversationRepository;

    @Override
    @Transactional(readOnly = true)
    public Conversation execute(UUID conversationId, UUID currentUserId) {
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.hasParticipant(currentUserId)) {
            throw new NotParticipantException();
        }

        return conversation;
    }
}
