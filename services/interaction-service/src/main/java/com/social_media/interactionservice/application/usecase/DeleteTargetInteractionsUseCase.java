package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.domain.model.TargetType;

import java.util.Collection;
import java.util.UUID;

public interface DeleteTargetInteractionsUseCase {
    CleanupResult execute(TargetType targetType, Collection<UUID> targetIds);

    record CleanupResult(int interactionsDeleted, int countersDeleted) {
    }
}
