package com.social_media.postservice.infrastructure.client.profile.service;


import com.social_media.common.api.ApiResponse;
import com.social_media.common.exception.BusinessRuleViolationException;
import com.social_media.common.exception.EntityNotFoundException;
import com.social_media.common.exception.ServiceUnavailableException;
import com.social_media.postservice.domain.model.post.valueobject.AuthorSnapshot;
import com.social_media.postservice.infrastructure.client.profile.ProfileClient;
import com.social_media.postservice.infrastructure.client.profile.dto.UserProfileResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceHelper {

    private final ProfileClient profileClient;

    @Retry(name = "profileRetry")
    @CircuitBreaker(name = "profileCircuitBreaker", fallbackMethod = "fallbackGetAuthorSnapshot")
    public AuthorSnapshot getAuthorSnapshot(UUID userId) {
        log.info("Đang gọi Profile Service lấy thông tin tác giả cho UserId: {}", userId);
        ApiResponse<UserProfileResponse> response = profileClient.getProfileById(userId);

        if (response != null && response.getData() != null) {
            UserProfileResponse profile = response.getData();
            return AuthorSnapshot.builder()
                    .name(profile.getFullName() != null && !profile.getFullName().isEmpty() ? profile.getFullName() : profile.getUsername())
                    .avatarUrl(profile.getAvatarUrl())
                    .build();
        }

        return getDefaultSnapshot();
    }

    public AuthorSnapshot fallbackGetAuthorSnapshot(UUID userId, Throwable throwable) {
        log.error("Fallback Profile-Service Active cho UserId: {}. Lý do: {}", userId, throwable.getMessage());
        return getDefaultSnapshot();
    }

    private AuthorSnapshot getDefaultSnapshot() {
        return AuthorSnapshot.builder()
                .name("Unknown")
                .avatarUrl(null)
                .build();
    }
}
