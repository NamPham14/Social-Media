package com.social_media.interactionservice.infrastructure.client;

import com.social_media.common.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.UUID;

@FeignClient(name = "post-service", contextId = "interactionPostClient", configuration = TargetFeignConfig.class)
public interface PostClient {
    @GetMapping("/api/v1/posts/{postId}")
    ApiResponse<PostSnapshot> getPost(@PathVariable("postId") UUID postId,
                                      @RequestHeader("X-Auth-User-Id") UUID actorId);

    record PostSnapshot(UUID id, UUID userId, String status) { }
}
