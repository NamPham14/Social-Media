package com.social_media.chatservice.application.usecase.impl;

import com.social_media.chatservice.application.exception.NotParticipantException;
import com.social_media.chatservice.application.usecase.SendMessageUseCase;
import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.model.aggregate.Message;
import com.social_media.chatservice.domain.model.enums.MessageType;
import com.social_media.chatservice.domain.repository.ConversationRepository;
import com.social_media.chatservice.domain.repository.MessageRepository;
import com.social_media.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendMessageUseCaseImpl implements SendMessageUseCase {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public Message execute(UUID conversationId, UUID senderId, String content, MessageType type) {
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation not found"));

        if (!conversation.hasParticipant(senderId)) {
            throw new NotParticipantException();
        }

        Message message = Message.send(conversation.getId(), senderId, content, type);
        return messageRepository.save(message);
    }
}
