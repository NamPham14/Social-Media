package com.social_media.postservice.infrastructure.client.interaction.service;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.config.security.SecurityUtils;
import com.social_media.postservice.infrastructure.client.interaction.InteractionServiceClient;
import com.social_media.postservice.infrastructure.client.interaction.dto.BatchPostLikedRequest;
import com.social_media.postservice.infrastructure.client.interaction.dto.BatchPostReactionRequest;
import com.social_media.postservice.infrastructure.client.interaction.dto.PostLikedResponse;
import com.social_media.postservice.infrastructure.client.interaction.dto.PostReactionResponse;
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
        BatchPostReactionRequest request = new BatchPostReactionRequest(postIds);

        ApiResponse<List<PostReactionResponse>> response = interactionServiceClient.getPostReactionCounts(request);

        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .collect(Collectors.toMap(PostReactionResponse::getPostId, PostReactionResponse::getReactionCount));
        }

        return Map.of();
    }

    public Map<UUID, Integer> fallbackGetLikeCounts(List<UUID> postIds, Throwable throwable) {
        log.warn("Fallback Interaction-Service: {}", throwable.getMessage());
        return Map.of();
    }

    @Retry(name = "interactionRetry")
    @CircuitBreaker(name = "interactionCircuitBreaker", fallbackMethod = "fallbackGetLikedByMe")
    public Map<UUID, Boolean> getLikedByMe(List<UUID> postIds) {
        UUID userId = SecurityUtils.getCurrentUserId();
        BatchPostLikedRequest request = new BatchPostLikedRequest(postIds);

        ApiResponse<List<PostLikedResponse>> response = interactionServiceClient.getPostLikedByMe(userId, request);

        if (response != null && response.getData() != null) {
            return response.getData().stream()
                    .collect(Collectors.toMap(PostLikedResponse::getPostId, PostLikedResponse::isLikedByMe));
        }

        return Map.of();
    }

    public Map<UUID, Boolean> fallbackGetLikedByMe(List<UUID> postIds, Throwable throwable) {
        log.warn("Fallback Interaction-Service getLikedByMe: {}", throwable.getMessage());
        return Map.of();
    }
}
