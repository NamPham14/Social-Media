package com.social_media.interactionservice.api.dto;

import com.social_media.interactionservice.domain.model.TargetType;

import java.util.UUID;

/**
 * Feed-ready reaction state for one target. The current V1 product supports LIKE only,
 * so likedByMe is the actor-specific state and reactionCount is the total LIKE count.
 */
public record InteractionSummaryResponse(
        TargetType targetType,
        UUID targetId,
        int reactionCount,
        boolean likedByMe
) { }
