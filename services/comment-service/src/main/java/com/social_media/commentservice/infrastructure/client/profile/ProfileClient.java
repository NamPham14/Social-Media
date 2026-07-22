package com.social_media.commentservice.infrastructure.client.profile;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "profile-service", path = "/api/v1/profile/users")
public interface ProfileClient {

    @GetMapping("/{userId}")
    Map<String, Object> getProfileById(@PathVariable("userId") UUID userId);
}
