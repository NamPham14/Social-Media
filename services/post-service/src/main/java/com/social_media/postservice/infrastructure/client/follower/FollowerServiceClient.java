package com.social_media.postservice.infrastructure.client.follower;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

// hiếu thêm
@FeignClient(
        name = "follower-service",
        path = "/api/v1/follow",
        configuration = FeignClientConfig.class
)
public interface FollowerServiceClient {

    @GetMapping("/internal/users/{id}/following-ids")
    ApiResponse<List<UUID>> getFollowingIds(@PathVariable("id") UUID userId);
}
