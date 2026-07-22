package com.social_media.interactionservice.infrastructure.repository;

import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import com.social_media.interactionservice.domain.model.InteractionPage;
import org.springframework.data.domain.PageRequest;

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

    @Override
    public List<Interaction> findActiveByActorAndTargets(UUID actorId, Collection<UUID> targetIds) {
        return targetIds.isEmpty() ? List.of() : interactionJpaRepository.findByUserIdAndTargetIdIn(actorId, targetIds);
    }

    @Override
    public InteractionPage findReactors(TargetType targetType, UUID targetId, int page, int size) {
        var result = interactionJpaRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
                targetType, targetId, PageRequest.of(page, size));
        return new InteractionPage(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public int removeAllByTargets(TargetType targetType, Collection<UUID> targetIds) {
        return targetIds.isEmpty() ? 0 : interactionJpaRepository.removeAllByTargets(targetType, targetIds);
    }
}
