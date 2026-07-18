package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteTargetInteractionsUseCaseImpl implements DeleteTargetInteractionsUseCase {

    private final InteractionRepository interactionRepository;
    private final InteractionCounterRepository counterRepository;

    @Override
    @Transactional
    public CleanupResult execute(TargetType targetType, Collection<UUID> targetIds) {
        List<UUID> distinctTargetIds = targetIds.stream().distinct().toList();
        int interactionsDeleted = interactionRepository.removeAllByTargets(targetType, distinctTargetIds);
        int countersDeleted = counterRepository.removeAllByTargets(targetType, distinctTargetIds);
        return new CleanupResult(interactionsDeleted, countersDeleted);
    }
}
