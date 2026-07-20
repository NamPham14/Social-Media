package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.CounterResponse;
import com.social_media.interactionservice.api.dto.TargetReferenceRequest;
import com.social_media.interactionservice.domain.model.TargetType;
import java.util.List;
import java.util.UUID;

public interface GetCountersUseCase {
    CounterResponse get(TargetType targetType, UUID targetId);
    List<CounterResponse> getBatch(List<TargetReferenceRequest> targets);
}
