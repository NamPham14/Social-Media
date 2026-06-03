package com.social_media.identityservice.infrastructure.client;


import com.social_media.identityservice.api.dto.ProfileCreationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "profile-service")
public interface ProfileClient {
    // Đường dẫn này phải khớp chính xác với API bên ProfileController
    @PostMapping("/api/v1/profile/internal/users")
    Object createProfile(@RequestBody ProfileCreationRequest request);
}
