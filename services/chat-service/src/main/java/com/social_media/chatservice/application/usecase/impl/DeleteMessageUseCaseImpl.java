package com.social_media.chatservice.application.usecase.impl;

import com.social_media.chatservice.application.exception.NotSenderException;
import com.social_media.chatservice.application.usecase.DeleteMessageUseCase;
import com.social_media.chatservice.domain.model.aggregate.Message;
import com.social_media.chatservice.domain.repository.MessageRepository;
import com.social_media.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteMessageUseCaseImpl implements DeleteMessageUseCase {
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public void execute(UUID messageId, UUID currentUserId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        if (!message.isSender(currentUserId)) {
            throw new NotSenderException();
        }

        message.deleteForSender(currentUserId);
        messageRepository.save(message);
    }
}
