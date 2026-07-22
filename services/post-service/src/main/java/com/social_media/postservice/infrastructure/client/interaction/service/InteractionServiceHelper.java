package com.social_media.postservice.infrastructure.client.interaction.service;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.interaction.InteractionServiceClient;
import com.social_media.postservice.infrastructure.client.interaction.dto.BatchCounterRequest;
import com.social_media.postservice.infrastructure.client.interaction.dto.CounterResponse;
import com.social_media.postservice.infrastructure.client.interaction.dto.TargetRef;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InteractionServiceHelper {

    private final InteractionServiceClient interactionServiceClient;

    @Retry(name = "interactionRetry")
    @CircuitBreaker(name = "interactionCircuitBreaker", fallbackMethod = "fallbackGetLikeCounts")
    public Map<UUID, Integer> getLikeCounts(List<UUID> postIds) {
        List<TargetRef> targets = postIds.stream()
                .map(id -> TargetRef.builder().targetType("POST").targetId(id).build())
                .toList();
        BatchCounterRequest request = new BatchCounterRequest(targets);

        ApiResponse<List<CounterResponse>> response = interactionServiceClient.getCountersBatch(request);

        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .collect(Collectors.toMap(CounterResponse::getTargetId, c -> c.getLikeCount() + c.getClapCount()));
        }

        return Map.of();
    }

    public Map<UUID, Integer> fallbackGetLikeCounts(List<UUID> postIds, Throwable throwable) {
        log.warn("Fallback Interaction-Service: {}", throwable.getMessage());
        return Map.of();
    }
}
