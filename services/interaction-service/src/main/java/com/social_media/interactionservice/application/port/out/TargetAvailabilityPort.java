package com.social_media.interactionservice.application.port.out;

import com.social_media.interactionservice.domain.model.TargetType;
import java.util.UUID;

public interface TargetAvailabilityPort {
    AvailableTarget getAvailable(TargetType targetType, UUID targetId, UUID actorId);

    record AvailableTarget(TargetType targetType, UUID targetId, UUID ownerId) {
    }
}
