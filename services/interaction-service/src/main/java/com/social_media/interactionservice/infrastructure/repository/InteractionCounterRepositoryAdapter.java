package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import com.social_media.interactionservice.domain.model.InteractionCounter;
import com.social_media.interactionservice.domain.model.InteractionCounterId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteractionCounterRepositoryAdapter implements InteractionCounterRepository {

    private final InteractionCounterJpaRepository interactionCounterJpaRepository;

    @Override
    public void increment(TargetType targetType, UUID targetId, ReactionType reactionType) {
        interactionCounterJpaRepository.insertIfMissing(targetType.name(), targetId);
        interactionCounterJpaRepository.increment(targetType.name(), targetId, reactionType.name());
    }

    @Override
    public void decrement(TargetType targetType, UUID targetId, ReactionType reactionType) {
        interactionCounterJpaRepository.decrement(targetType.name(), targetId, reactionType.name());
    }

    @Override
    public Optional<InteractionCounter> find(TargetType targetType, UUID targetId) {
        return interactionCounterJpaRepository.findById(new InteractionCounterId(targetType, targetId));
    }

    @Override
    public List<InteractionCounter> findAll(Collection<InteractionCounterId> ids) {
        return interactionCounterJpaRepository.findAllById(ids);
    }

    @Override
    public int removeAllByTargets(TargetType targetType, Collection<UUID> targetIds) {
        return targetIds.isEmpty() ? 0 : interactionCounterJpaRepository.removeAllByTargets(targetType, targetIds);
    }
}
