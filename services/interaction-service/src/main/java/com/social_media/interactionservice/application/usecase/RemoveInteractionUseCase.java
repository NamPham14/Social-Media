package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import java.util.UUID;

public interface RemoveInteractionUseCase {
    boolean execute(UUID actorId, TargetType targetType, UUID targetId, ReactionType reactionType);
}
