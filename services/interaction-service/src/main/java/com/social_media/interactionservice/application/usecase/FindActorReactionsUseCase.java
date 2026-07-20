package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.InteractionResponse;
import com.social_media.interactionservice.domain.model.TargetType;
import java.util.List;
import java.util.UUID;

public interface FindActorReactionsUseCase {
    List<InteractionResponse> execute(UUID actorId, TargetType targetType, UUID targetId);
}
