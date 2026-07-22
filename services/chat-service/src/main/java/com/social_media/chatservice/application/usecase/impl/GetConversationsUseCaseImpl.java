package com.social_media.chatservice.application.usecase.impl;

import com.social_media.chatservice.application.usecase.GetConversationsUseCase;
import com.social_media.chatservice.domain.model.aggregate.Conversation;
import com.social_media.chatservice.domain.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetConversationsUseCaseImpl implements GetConversationsUseCase {
    private final ConversationRepository conversationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Conversation> execute(UUID userId) {
        return conversationRepository.findByUserId(userId);
    }
}
