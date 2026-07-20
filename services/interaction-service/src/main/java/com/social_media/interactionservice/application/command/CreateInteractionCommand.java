package com.social_media.interactionservice.application.command;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;

import java.util.UUID;

public record CreateInteractionCommand(
        UUID actorId,
        TargetType targetType,
        UUID targetId,
        ReactionType reactionType
) {
}
