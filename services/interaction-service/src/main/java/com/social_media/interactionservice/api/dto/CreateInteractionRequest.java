package com.social_media.interactionservice.api.dto;

import com.social_media.interactionservice.domain.model.ReactionType;
import com.social_media.interactionservice.domain.model.TargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateInteractionRequest {

    @NotNull
    private TargetType targetType;

    @NotNull
    private UUID targetId;

    @NotNull
    private ReactionType reactionType;
}
