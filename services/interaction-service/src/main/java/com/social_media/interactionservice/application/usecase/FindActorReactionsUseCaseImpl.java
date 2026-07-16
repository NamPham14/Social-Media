package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindActorReactionsUseCaseImpl implements FindActorReactionsUseCase {
    private final InteractionRepository interactionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InteractionResponse> execute(UUID actorId, TargetType targetType, UUID targetId) {
        return interactionRepository.findActiveByActorAndTarget(actorId, targetType, targetId).stream()
                .map(this::response).toList();
    }

    private InteractionResponse response(Interaction interaction) {
        return InteractionResponse.builder().interactionId(interaction.getId()).userId(interaction.getUserId())
                .targetType(interaction.getTargetType()).targetId(interaction.getTargetId())
                .reactionType(interaction.getReactionType()).created(false).duplicateIgnored(false).build();
    }
}
