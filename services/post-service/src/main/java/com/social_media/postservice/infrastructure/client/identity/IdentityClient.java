package com.social_media.postservice.infrastructure.client.identity;


import com.social_media.postservice.infrastructure.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "identity-service",
        path = "/api/v1/identity",
        configuration = FeignClientConfig.class)
//@FeignClient(name = "identity-service", path = "/api/v1/identity")
public interface IdentityClient {

    @GetMapping("/{userId}/status")
    String getUserStatus(@PathVariable("userId") UUID userId);

}
