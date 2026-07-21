package com.social_media.postservice.infrastructure.client.profile;


import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.config.FeignClientConfig;
import com.social_media.postservice.infrastructure.client.profile.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "profile-service",
        path = "/api/v1/profiles",
        configuration = FeignClientConfig.class
)
public interface ProfileClient {

    @GetMapping("/{userId}")
    ApiResponse<UserProfileResponse> getProfileById(@PathVariable("userId") UUID userId);
}