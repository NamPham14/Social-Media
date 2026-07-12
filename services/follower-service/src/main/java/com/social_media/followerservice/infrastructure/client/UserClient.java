package com.social_media.followerservice.infrastructure.client;

import com.social_media.followerservice.infrastructure.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserClient {
    
    @GetMapping("/api/v1/users/profiles")
    Object getUserProfilesByIds(@RequestParam("ids") List<UUID> ids);
}
