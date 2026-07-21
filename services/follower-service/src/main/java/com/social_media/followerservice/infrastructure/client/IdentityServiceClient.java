package com.social_media.followerservice.infrastructure.client;

import com.social_media.followerservice.infrastructure.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "identity-service", configuration = FeignClientConfig.class)
public interface IdentityServiceClient {

    @GetMapping("/api/v1/identity/{userId}/status")
    String getUserStatus(@PathVariable("userId") UUID userId);
}
