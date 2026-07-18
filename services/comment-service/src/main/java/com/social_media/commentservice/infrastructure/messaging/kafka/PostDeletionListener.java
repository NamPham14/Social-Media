package com.social_media.commentservice.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.commentservice.application.event.PostDeletedEvent;
import com.social_media.commentservice.application.usecase.DeleteCommentsByPostUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostDeletionListener {

    private final ObjectMapper objectMapper;
    private final DeleteCommentsByPostUseCase deleteCommentsByPostUseCase;

    @KafkaListener(topics = "${messaging.topics.post-deleted}")
    public void onPostDeleted(String payload) throws Exception {
        JsonNode json = objectMapper.readTree(payload);
        PostDeletedEvent event = new PostDeletedEvent(
                requiredText(json, "id"),
                UUID.fromString(requiredText(json, "postId")),
                UUID.fromString(requiredText(json, "authorId"))
        );
        int deleted = deleteCommentsByPostUseCase.execute(event);
        log.info("Consumed post deletion eventId={} postId={} commentsDeleted={}",
                event.id(), event.postId(), deleted);
    }

    private String requiredText(JsonNode json, String field) {
        String value = json.path(field).asText(null);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing post deletion field: " + field);
        }
        return value;
    }
}
