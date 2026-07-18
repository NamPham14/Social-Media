package com.social_media.interactionservice.domain.repository;

import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import java.util.List;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface InteractionRepository {
    boolean insertIfAbsent(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType);
    boolean remove(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType);
    Optional<Interaction> find(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType);
    List<Interaction> findActiveByActorAndTarget(UUID actorId, TargetType targetType, UUID targetId);
    int removeAllByTargets(TargetType targetType, Collection<UUID> targetIds);
}
