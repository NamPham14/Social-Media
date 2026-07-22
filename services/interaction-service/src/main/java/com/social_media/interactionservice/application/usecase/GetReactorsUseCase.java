package com.social_media.interactionservice.application.usecase;

import com.social_media.common.api.PageResponse;
import com.social_media.interactionservice.api.dto.ReactorResponse;
import com.social_media.interactionservice.domain.model.TargetType;

import java.util.UUID;

public interface GetReactorsUseCase {
    PageResponse<ReactorResponse> execute(UUID actorId, TargetType targetType, UUID targetId, int page, int size);
}
