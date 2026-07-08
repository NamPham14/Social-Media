package com.social_media.notificationservice.infrastructure.client.post;

import com.social_media.common.api.ApiResponse;
import com.social_media.notificationservice.infrastructure.client.post.dto.PostClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "post-service")
public interface PostClient {

    @GetMapping("/api/v1/posts/{postId}")
    ApiResponse<PostClientResponse> getPostById(@PathVariable("postId") Long postId);
}