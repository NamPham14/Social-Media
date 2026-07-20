package com.social_media.interactionservice.infrastructure.client;

import com.social_media.interactionservice.application.port.out.TargetAvailabilityPort;
import com.social_media.interactionservice.domain.model.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeignTargetAvailabilityAdapter implements TargetAvailabilityPort {
    private final PostAvailabilityChecker postChecker;
    private final CommentAvailabilityChecker commentChecker;

    @Override
    public AvailableTarget getAvailable(TargetType targetType, UUID targetId, UUID actorId) {
        UUID ownerId = targetType == TargetType.POST
                ? postChecker.ensure(targetId, actorId)
                : commentChecker.ensure(targetId);
        return new AvailableTarget(targetType, targetId, ownerId);
    }
}
