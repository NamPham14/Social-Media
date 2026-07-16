package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InteractionRepositoryAdapter implements InteractionRepository {

    private final InteractionJpaRepository interactionJpaRepository;

    @Override
    public boolean insertIfAbsent(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        return interactionJpaRepository.insertIfAbsent(UUID.randomUUID(), actorId, targetType.name(), targetId,
                reactionType.name()) == 1;
    }

    @Override
    public boolean remove(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        return interactionJpaRepository.remove(actorId, targetType, targetId, reactionType) == 1;
    }

    @Override
    public Optional<Interaction> find(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType) {
        return interactionJpaRepository.findByUserIdAndTargetTypeAndTargetIdAndReactionType(
                actorId, targetType, targetId, reactionType);
    }

    @Override
    public List<Interaction> findActiveByActorAndTarget(UUID actorId, TargetType targetType, UUID targetId) {
        return interactionJpaRepository.findByUserIdAndTargetTypeAndTargetIdOrderByReactionTypeAsc(
                actorId, targetType, targetId);
    }
}
