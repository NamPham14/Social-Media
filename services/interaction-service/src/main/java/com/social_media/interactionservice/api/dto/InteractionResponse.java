package com.social_media.interactionservice.api.dto;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class InteractionResponse {
    private UUID interactionId;
    private UUID userId;
    private TargetType targetType;
    private UUID targetId;
    private ReactionType reactionType;
    private boolean created;
    private boolean duplicateIgnored;
}
