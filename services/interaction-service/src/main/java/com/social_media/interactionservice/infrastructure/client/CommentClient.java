package com.social_media.interactionservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.UUID;

@FeignClient(name = "comment-service", contextId = "interactionCommentClient", configuration = TargetFeignConfig.class)
public interface CommentClient {
    @GetMapping("/internal/v1/comments/{commentId}/availability")
    Availability getAvailability(@PathVariable UUID commentId,
                                 @RequestHeader("X-Internal-Service-Token") String serviceToken);

    record Availability(UUID targetId, boolean available, String reason) { }
}
