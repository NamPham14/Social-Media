package com.social_media.interactionservice.application.usecase;

import com.social_media.interactionservice.api.dto.CounterResponse;
import com.social_media.interactionservice.api.dto.PostReactionResponse;
import com.social_media.interactionservice.api.dto.TargetReferenceRequest;
import com.social_media.interactionservice.domain.model.InteractionCounter;
import com.social_media.interactionservice.domain.model.InteractionCounterId;
import com.social_media.interactionservice.domain.model.TargetType;
import com.social_media.interactionservice.domain.repository.InteractionCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GetCountersUseCaseImpl implements GetCountersUseCase {
    private final InteractionCounterRepository counterRepository;

    @Override
    @Transactional(readOnly = true)
    public CounterResponse get(TargetType type, UUID id) {
        return counterRepository.find(type, id).map(this::response)
                .orElse(new CounterResponse(type, id, 0));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CounterResponse> getBatch(List<TargetReferenceRequest> targets) {
        List<InteractionCounterId> ids = targets.stream()
                .map(t -> new InteractionCounterId(t.targetType(), t.targetId())).distinct().toList();
        Map<InteractionCounterId, InteractionCounter> found = new HashMap<>();
        counterRepository.findAll(ids).forEach(c -> found.put(c.getId(), c));
        return ids.stream().map(id -> Optional.ofNullable(found.get(id)).map(this::response)
                .orElse(new CounterResponse(id.getTargetType(), id.getTargetId(), 0))).toList();
    }

    // Hiếu thêm
    @Override
    @Transactional(readOnly = true)
    public List<PostReactionResponse> getBatchByPostIds(List<UUID> postIds) {
        List<InteractionCounterId> ids = postIds.stream()
                .map(id -> new InteractionCounterId(TargetType.POST, id)).distinct().toList();
        Map<InteractionCounterId, InteractionCounter> found = new HashMap<>();
        counterRepository.findAll(ids).forEach(c -> found.put(c.getId(), c));
        return ids.stream().map(id -> Optional.ofNullable(found.get(id))
                .map(c -> new PostReactionResponse(id.getTargetId(), c.getLikeCount()))
                .orElse(new PostReactionResponse(id.getTargetId(), 0))).toList();
    }

    private CounterResponse response(InteractionCounter c) {
        return new CounterResponse(c.getId().getTargetType(), c.getId().getTargetId(), c.getLikeCount());
    }
}
