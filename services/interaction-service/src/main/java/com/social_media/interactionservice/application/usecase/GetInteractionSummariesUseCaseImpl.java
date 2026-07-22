package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.InteractionSummaryResponse;
import com.social_media.interactionservice.api.dto.TargetReferenceRequest;
import com.social_media.interactionservice.domain.model.Interaction;
import com.social_media.interactionservice.domain.model.InteractionCounter;
import com.social_media.interactionservice.domain.model.InteractionCounterId;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import com.social_media.interactionservice.domain.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetInteractionSummariesUseCaseImpl implements GetInteractionSummariesUseCase {
    private final InteractionCounterRepository counterRepository;
    private final InteractionRepository interactionRepository;

    @Override
    @Transactional(readOnly = true)
    public InteractionSummaryResponse get(UUID actorId, TargetType targetType, UUID targetId) {
        return getBatch(actorId, List.of(new TargetReferenceRequest(targetType, targetId))).getFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteractionSummaryResponse> getBatch(UUID actorId, List<TargetReferenceRequest> targets) {
        List<InteractionCounterId> ids = targets.stream()
                .map(target -> new InteractionCounterId(target.targetType(), target.targetId()))
                .distinct()
                .toList();

        Map<InteractionCounterId, Integer> counts = new HashMap<>();
        for (InteractionCounter counter : counterRepository.findAll(ids)) {
            counts.put(counter.getId(), counter.getLikeCount());
        }

        Set<InteractionCounterId> likedTargets = new HashSet<>();
        if (actorId != null) {
            Set<InteractionCounterId> requestedTargets = new HashSet<>(ids);
            List<UUID> targetIds = ids.stream().map(InteractionCounterId::getTargetId).toList();
            for (Interaction interaction : interactionRepository.findActiveByActorAndTargets(actorId, targetIds)) {
                InteractionCounterId key = new InteractionCounterId(
                        interaction.getTargetType(), interaction.getTargetId());
                if (requestedTargets.contains(key)) {
                    likedTargets.add(key);
                }
            }
        }

        return ids.stream()
                .map(id -> new InteractionSummaryResponse(
                        id.getTargetType(), id.getTargetId(), counts.getOrDefault(id, 0), likedTargets.contains(id)))
                .toList();
    }
}
