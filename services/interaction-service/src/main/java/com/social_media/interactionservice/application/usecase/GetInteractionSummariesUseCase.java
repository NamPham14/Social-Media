package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.InteractionSummaryResponse;
import com.social_media.interactionservice.api.dto.PostLikedResponse;
import com.social_media.interactionservice.api.dto.TargetReferenceRequest;
import com.social_media.interactionservice.domain.model.TargetType;

import java.util.List;
import java.util.UUID;

public interface GetInteractionSummariesUseCase {
    InteractionSummaryResponse get(UUID actorId, TargetType targetType, UUID targetId);
    List<InteractionSummaryResponse> getBatch(UUID actorId, List<TargetReferenceRequest> targets);

    // Hiếu thêm
    List<PostLikedResponse> getBatchLikedByMe(UUID actorId, List<UUID> postIds);
}
