package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoveInteractionUseCaseImpl implements RemoveInteractionUseCase {
    private final InteractionRepository interactionRepository;
    private final InteractionCounterRepository counterRepository;

    @Override
    @Transactional
    public boolean execute(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        boolean removed = interactionRepository.remove(actorId, targetType, targetId, reactionType);
        if (removed) counterRepository.decrement(targetType, targetId, reactionType);
        return removed;
    }
}
