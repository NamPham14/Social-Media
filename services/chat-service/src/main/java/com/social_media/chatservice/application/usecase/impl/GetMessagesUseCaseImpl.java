package com.social_media.chatservice.application.usecase.impl;

import com.social_media.chatservice.application.exception.NotParticipantException;
import com.social_media.chatservice.application.usecase.GetMessagesUseCase;
import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.model.aggregate.Message;
import com.social_media.chatservice.domain.repository.ConversationRepository;
import com.social_media.chatservice.domain.repository.MessageRepository;
import com.social_media.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetMessagesUseCaseImpl implements GetMessagesUseCase {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Message> execute(UUID conversationId, UUID currentUserId, Pageable pageable) {
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.hasParticipant(currentUserId)) {
            throw new NotParticipantException();
        }

        return messageRepository.findByConversationId(conversation.getId(), pageable);
    }
}
