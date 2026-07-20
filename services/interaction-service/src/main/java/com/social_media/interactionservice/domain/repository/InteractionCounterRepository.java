package com.social_media.interactionservice.domain.repository;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;

import java.util.UUID;
import com.social_media.interactionservice.domain.model.InteractionCounter;
import com.social_media.interactionservice.domain.model.InteractionCounterId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InteractionCounterRepository {
    void increment(TargetType targetType, UUID targetId, ReactionType reactionType);
    void decrement(TargetType targetType, UUID targetId, ReactionType reactionType);
    Optional<InteractionCounter> find(TargetType targetType, UUID targetId);
    List<InteractionCounter> findAll(Collection<InteractionCounterId> ids);
    int removeAllByTargets(TargetType targetType, Collection<UUID> targetIds);
}
