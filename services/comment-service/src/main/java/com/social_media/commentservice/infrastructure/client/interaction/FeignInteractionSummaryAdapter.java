package com.social_media.commentservice.infrastructure.client.interaction;

import com.social_media.commentservice.application.port.out.InteractionSummaryPort;
import com.social_media.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignInteractionSummaryAdapter implements InteractionSummaryPort {
    private final InteractionSummaryClient client;

    @Override
    public Map<UUID, Summary> getCommentSummaries(UUID actorId, Collection<UUID> commentIds) {
        if (commentIds.isEmpty()) return Map.of();
        var targets = commentIds.stream().distinct()
                .map(id -> new InteractionSummaryClient.TargetReference("COMMENT", id)).toList();
        ApiResponse<java.util.List<InteractionSummaryClient.SummaryResponse>> response;
        try {
            response = client.getSummaries(actorId, new InteractionSummaryClient.BatchRequest(targets));
        } catch (RuntimeException exception) {
            // Engagement metadata is optional on a read path. Keep comments readable while the
            // interaction service recovers; the default values are applied by the enricher.
            log.warn("Interaction summary enrichment unavailable for {} comments", targets.size(), exception);
            return Map.of();
        }
        Map<UUID, Summary> result = new LinkedHashMap<>();
        if (response != null && response.getData() != null) {
            response.getData().forEach(item -> result.put(item.targetId(),
                    new Summary(item.reactionCount(), item.likedByMe())));
        }
        return result;
    }
}
