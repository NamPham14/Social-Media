package com.social_media.interactionservice.application.port.out;

import com.social_media.interactionservice.domain.model.TargetType;
import java.util.UUID;

public interface TargetAvailabilityPort {
    void ensureAvailable(TargetType targetType, UUID targetId, UUID actorId);
}
