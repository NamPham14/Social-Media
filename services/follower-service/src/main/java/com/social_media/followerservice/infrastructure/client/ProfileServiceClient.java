package com.social_media.followerservice.infrastructure.client;

import com.social_media.common.api.ApiResponse;
import com.social_media.followerservice.infrastructure.client.config.FeignClientConfig;
import com.social_media.followerservice.infrastructure.client.identity.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "profile-service", configuration = FeignClientConfig.class)
public interface ProfileServiceClient {

    @GetMapping("/api/v1/profile/users/{id}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable("id") UUID id);
}
