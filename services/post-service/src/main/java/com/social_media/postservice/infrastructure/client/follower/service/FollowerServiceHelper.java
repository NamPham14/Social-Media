package com.social_media.postservice.infrastructure.client.follower.service;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.follower.FollowerServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

// hiếu thêm
@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerServiceHelper {

    private final FollowerServiceClient followerServiceClient;

    @Retry(name = "followerRetry")
    @CircuitBreaker(name = "followerCircuitBreaker", fallbackMethod = "fallbackGetFollowingIds")
    public List<UUID> getFollowingIds(UUID userId) {
        log.info("Gọi Follower Service lấy danh sách following cho UserId: {}", userId);
        ApiResponse<List<UUID>> response = followerServiceClient.getFollowingIds(userId);

        if (response != null && response.getData() != null) {
            return response.getData();
        }

        return List.of();
    }

    public List<UUID> fallbackGetFollowingIds(UUID userId, Throwable throwable) {
        log.warn("Fallback Follower-Service cho UserId: {}. Lý do: {}", userId, throwable.getMessage());
        return List.of();
    }
}
