package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InteractionCounterRepositoryAdapter implements InteractionCounterRepository {

    private final InteractionCounterJpaRepository interactionCounterJpaRepository;

    @Override
    public void increment(TargetType targetType, UUID targetId, ReactionType reactionType) {
        interactionCounterJpaRepository.insertIfMissing(targetType.name(), targetId);
        interactionCounterJpaRepository.increment(targetType.name(), targetId, reactionType.name());
    }
}
