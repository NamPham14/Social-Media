package com.social_media.chatservice.infrastructure.client;

import com.social_media.chatservice.infrastructure.client.dto.FollowClientResponse;
import com.social_media.common.api.ApiResponse;
import com.social_media.common.api.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "follower-service")
public interface FollowerClient {

    @GetMapping("/api/v1/follow/users/{id}/following")
    ApiResponse<PageResponse<FollowClientResponse>> getFollowing(
            @PathVariable("id") UUID id,
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}
