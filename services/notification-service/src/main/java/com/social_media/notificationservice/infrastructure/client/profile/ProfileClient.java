package com.social_media.notificationservice.infrastructure.client.profile;

import com.social_media.common.api.ApiResponse;
import com.social_media.notificationservice.infrastructure.client.profile.dto.ProfileClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "profile-service")
public interface ProfileClient {

    @GetMapping("/api/v1/profile/users/{id}")
    ApiResponse<ProfileClientResponse> getProfile(@PathVariable("id") UUID id);
}
