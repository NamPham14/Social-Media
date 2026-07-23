package com.social_media.notificationservice.infrastructure.client.follower;

import com.social_media.common.api.ApiResponse;
import com.social_media.common.utils.SecurityConstants;
import com.social_media.notificationservice.infrastructure.client.follower.dto.FollowerIdPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "follower-service")
public interface FollowerClient {
    @GetMapping("/api/v1/follow/internal/users/{authorId}/follower-ids")
    ApiResponse<FollowerIdPageResponse> getFollowerIds(
            @RequestHeader(SecurityConstants.HEADER_USER_ID) String requesterId,
            @PathVariable("authorId") String authorId,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
