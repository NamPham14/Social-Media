package com.social_media.commentservice.infrastructure.client.interaction;

import com.social_media.common.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "interaction-service", contextId = "commentInteractionSummaryClient")
public interface InteractionSummaryClient {
    @PostMapping("/api/v1/interactions/summaries/batch")
    ApiResponse<List<SummaryResponse>> getSummaries(
            @RequestHeader(value = "X-Auth-User-Id", required = false) UUID actorId,
            @RequestBody BatchRequest request);

    record BatchRequest(List<TargetReference> targets) { }
    record TargetReference(String targetType, UUID targetId) { }
    record SummaryResponse(String targetType, UUID targetId, int reactionCount, boolean likedByMe) { }
}
