package com.social_media.commentservice.infrastructure.client.profile;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.commentservice.infrastructure.repository.CommentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileEventConsumer {

    private final ObjectMapper objectMapper;
    private final CommentJpaRepository commentJpaRepository;

    @KafkaListener(topics = "profile-events", groupId = "comment-group")
    @Transactional
    public void consumeProfileEvent(String message) {
        log.debug("Received profile snapshot update");
        try {
            JsonNode payload = objectMapper.readTree(message);
            UUID userId = UUID.fromString(payload.get("userId").asText());
            String newName = payload.has("authorName") && !payload.get("authorName").isNull() ? payload.get("authorName").asText() : null;
            String newAvatar = payload.has("authorAvatarUrl") && !payload.get("authorAvatarUrl").isNull() ? payload.get("authorAvatarUrl").asText() : null;

            commentJpaRepository.updateAuthorInfo(userId, newName, newAvatar);

            log.info("Updated comment author snapshot userId={}", userId);
        } catch (Exception e) {
            log.error("Xử lý profile event thất bại; Kafka error handler sẽ retry/DLT", e);
            throw new IllegalArgumentException("Invalid profile event", e);
        }
    }
}
