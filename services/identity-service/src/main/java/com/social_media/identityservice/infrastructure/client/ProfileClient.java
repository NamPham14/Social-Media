package com.social_media.identityservice.infrastructure.client;

import com.social_media.common.api.ApiResponse;
import com.social_media.identityservice.api.dto.request.ProfileCreationRequest;
import com.social_media.identityservice.api.dto.response.ProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "profile-service", url = "${app.services.profile-service.url:http://localhost:8082}",
        configuration = FeignConfig.class)
public interface ProfileClient {

    @PostMapping("/api/v1/profile/internal/users")
    ApiResponse<Object> createProfile(@RequestBody ProfileCreationRequest request);

    // Hàm để Admin gọi sang xin thông tin Profile chi tiết
    @GetMapping("/api/v1/profile/users/{id}")
    ApiResponse<ProfileResponse> getProfile(@PathVariable("id") UUID id);
}
