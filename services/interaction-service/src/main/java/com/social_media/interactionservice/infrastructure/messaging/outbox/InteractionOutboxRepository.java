package com.social_media.interactionservice.infrastructure.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.interactionservice.application.event.ReactionNotificationEvent;
import com.social_media.interactionservice.application.port.out.InteractionEventOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InteractionOutboxRepository implements InteractionEventOutbox {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Value("${messaging.topics.reaction-created}")
    private String reactionCreatedTopic;

    @Override
    public void append(ReactionNotificationEvent event) {
        try {
            jdbcTemplate.update("""
                    INSERT INTO interaction_outbox
                        (event_id, aggregate_id, event_type, topic, payload, status, next_attempt_at)
                    VALUES (?, ?, ?, ?, CAST(? AS jsonb), 'PENDING', NOW())
                    """, event.eventId(), event.interactionId(), event.eventType(), reactionCreatedTopic,
                    objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException failure) {
            throw new IllegalArgumentException("Could not serialize interaction integration event", failure);
        }
    }

    public List<InteractionOutboxMessage> lockPendingBatch(int batchSize) {
        return jdbcTemplate.query("""
                SELECT event_id, aggregate_id, event_type, topic, payload::text, attempts
                FROM interaction_outbox
                WHERE status = 'PENDING' AND next_attempt_at <= NOW()
                ORDER BY created_at
                LIMIT ?
                FOR UPDATE SKIP LOCKED
                """, (rs, rowNum) -> new InteractionOutboxMessage(
                rs.getObject("event_id", UUID.class),
                rs.getObject("aggregate_id", UUID.class),
                rs.getString("event_type"),
                rs.getString("topic"),
                rs.getString("payload"),
                rs.getInt("attempts")
        ), batchSize);
    }

    public void markPublished(UUID eventId) {
        jdbcTemplate.update("""
                UPDATE interaction_outbox
                SET status = 'PUBLISHED', published_at = NOW(), last_error = NULL
                WHERE event_id = ? AND status = 'PENDING'
                """, eventId);
    }

    public void markFailed(UUID eventId, String error, Duration retryDelay) {
        String safeError = error == null ? "Unknown Kafka publish failure"
                : error.substring(0, Math.min(error.length(), 2000));
        jdbcTemplate.update("""
                UPDATE interaction_outbox
                SET attempts = attempts + 1, last_error = ?, next_attempt_at = ?
                WHERE event_id = ? AND status = 'PENDING'
                """, safeError, Timestamp.from(Instant.now().plus(retryDelay)), eventId);
    }

    public int deletePublishedBefore(Instant threshold) {
        return jdbcTemplate.update("""
                DELETE FROM interaction_outbox
                WHERE status = 'PUBLISHED' AND published_at < ?
                """, Timestamp.from(threshold));
    }
}
