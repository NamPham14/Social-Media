package com.social_media.interactionservice.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.interactionservice.application.usecase.DeleteTargetInteractionsUseCase;
import com.social_media.interactionservice.domain.model.TargetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TargetDeletionListener {

    private final ObjectMapper objectMapper;
    private final DeleteTargetInteractionsUseCase cleanupUseCase;

    @KafkaListener(topics = "${messaging.topics.post-deleted}")
    public void onPostDeleted(String payload) throws Exception {
        JsonNode json = objectMapper.readTree(payload);
        String eventId = requiredText(json, "id");
        UUID postId = UUID.fromString(requiredText(json, "postId"));
        var result = cleanupUseCase.execute(TargetType.POST, List.of(postId));
        logResult(eventId, TargetType.POST, 1, result);
    }

    @KafkaListener(topics = "${messaging.topics.post-comments-deleted}")
    public void onPostCommentsDeleted(String payload) throws Exception {
        JsonNode json = objectMapper.readTree(payload);
        String eventId = requiredText(json, "id");
        JsonNode idsNode = json.path("commentIds");
        if (!idsNode.isArray()) {
            throw new IllegalArgumentException("Missing deleted commentIds array");
        }
        List<UUID> commentIds = new ArrayList<>();
        idsNode.forEach(node -> commentIds.add(UUID.fromString(node.asText())));
        var result = cleanupUseCase.execute(TargetType.COMMENT, commentIds);
        logResult(eventId, TargetType.COMMENT, commentIds.size(), result);
    }

    @KafkaListener(topics = "${messaging.topics.comment-deleted}")
    public void onCommentDeleted(String payload) throws Exception {
        JsonNode json = objectMapper.readTree(payload);
        String eventId = requiredText(json, "id");
        UUID commentId = UUID.fromString(requiredText(json, "commentId"));
        var result = cleanupUseCase.execute(TargetType.COMMENT, List.of(commentId));
        logResult(eventId, TargetType.COMMENT, 1, result);
    }

    private void logResult(String eventId, TargetType targetType, int targetCount,
                           DeleteTargetInteractionsUseCase.CleanupResult result) {
        log.info("Consumed target deletion eventId={} targetType={} targetCount={} interactionsDeleted={} countersDeleted={}",
                eventId, targetType, targetCount, result.interactionsDeleted(), result.countersDeleted());
    }

    private String requiredText(JsonNode json, String field) {
        String value = json.path(field).asText(null);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing target deletion field: " + field);
        }
        return value;
    }
}
