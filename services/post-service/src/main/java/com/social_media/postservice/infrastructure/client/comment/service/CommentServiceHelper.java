package com.social_media.postservice.infrastructure.client.comment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.comment.CommentServiceClient;
import com.social_media.postservice.infrastructure.client.comment.dto.BatchCommentCountRequest;
import com.social_media.postservice.infrastructure.client.comment.dto.CommentCountResponse;
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
public class CommentServiceHelper {

    private final CommentServiceClient commentServiceClient;

    @Retry(name = "commentRetry")
    @CircuitBreaker(name = "commentCircuitBreaker", fallbackMethod = "fallbackGetCommentCounts")
    public Map<UUID, Integer> getCommentCounts(List<UUID> postIds) {
        BatchCommentCountRequest request = new BatchCommentCountRequest(postIds);

        ApiResponse<List<CommentCountResponse>> response = commentServiceClient.getCommentCounts(request);

        if (response != null && response.getData() != null) {
            ObjectMapper mapper = new ObjectMapper();
            return response.getData().stream()
                    .map(obj -> mapper.convertValue(obj, CommentCountResponse.class))
                    .collect(Collectors.toMap(CommentCountResponse::getPostId, r -> (int) r.getCommentCount()));
        }

        return Map.of();
    }

    public Map<UUID, Integer> fallbackGetCommentCounts(List<UUID> postIds, Throwable throwable) {
        log.warn("Fallback Comment-Service: {}", throwable.getMessage());
        return Map.of();
    }
}
