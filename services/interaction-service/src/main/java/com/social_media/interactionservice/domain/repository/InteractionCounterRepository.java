package com.social_media.interactionservice.domain.repository;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;

import java.util.UUID;

public interface InteractionCounterRepository {
    void increment(TargetType targetType, UUID targetId, ReactionType reactionType);
}
