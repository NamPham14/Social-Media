package com.social_media.followerservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/api/v1/users/profiles")
    Object getUserProfilesByIds(@RequestParam("ids") List<UUID> ids);
}
