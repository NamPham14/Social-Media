package com.social_media.identityservice.infrastructure.client;

import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.api.dto.request.ProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "profile-service", url = "${app.services.profile-service.url:http://localhost:8082}")
public interface ProfileClient {
    @PostMapping("/api/v1/profile/internal/users")
    ApiResponse<Object> createProfile(@RequestBody ProfileCreationRequest request);
}
