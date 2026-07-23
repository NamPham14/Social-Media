package com.social_media.postservice.infrastructure.client.interaction;

import com.social_media.common.api.ApiResponse;
import com.social_media.postservice.infrastructure.client.config.FeignClientConfig;
import com.social_media.postservice.infrastructure.client.interaction.dto.BatchPostLikedRequest;
import com.social_media.postservice.infrastructure.client.interaction.dto.BatchPostReactionRequest;
import com.social_media.postservice.infrastructure.client.interaction.dto.PostLikedResponse;
import com.social_media.postservice.infrastructure.client.interaction.dto.PostReactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "interaction-service",
        path = "/api/v1",
        configuration = FeignClientConfig.class
)
public interface InteractionServiceClient {

    @PostMapping("/internal/posts/reaction-counts")
    ApiResponse<List<PostReactionResponse>> getPostReactionCounts(@RequestBody BatchPostReactionRequest request);


    @PostMapping("/internal/posts/liked-by-me")
    ApiResponse<List<PostLikedResponse>> getPostLikedByMe(
            @RequestBody BatchPostLikedRequest request);
}
