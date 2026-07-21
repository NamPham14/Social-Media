package com.social_media.commentservice.infrastructure.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social_media.commentservice.application.event.CommentNotificationEvent;
import com.social_media.commentservice.application.event.PostCommentsDeletedEvent;
import com.social_media.commentservice.application.port.out.CommentEventOutbox;
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
public class CommentOutboxRepository implements CommentEventOutbox {

    private static final String EVENT_TYPE = "PostCommentsDeletedV1";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Value("${messaging.topics.post-comments-deleted}")
    private String postCommentsDeletedTopic;

    @Value("${messaging.topics.comment-created}")
    private String commentCreatedTopic;

    @Value("${messaging.topics.comment-replied}")
    private String commentRepliedTopic;

    @Override
    public void append(PostCommentsDeletedEvent event) {
        persist(UUID.fromString(event.id()), event.postId(), EVENT_TYPE, postCommentsDeletedTopic, event);
    }

    @Override
    public void append(CommentNotificationEvent event) {
        String topic = CommentNotificationEvent.COMMENT_CREATED.equals(event.eventType())
                ? commentCreatedTopic : commentRepliedTopic;
        persist(event.eventId(), event.commentId(), event.eventType(), topic, event);
    }

    private void persist(UUID eventId, UUID aggregateId, String eventType, String topic, Object event) {
        try {
            jdbcTemplate.update("""
                    INSERT INTO comment_outbox
                        (event_id, aggregate_id, event_type, topic, payload, status, next_attempt_at)
                    VALUES (?, ?, ?, ?, CAST(? AS jsonb), 'PENDING', NOW())
                    """, eventId, aggregateId, eventType, topic, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException failure) {
            throw new IllegalArgumentException("Could not serialize comment integration event", failure);
        }
    }

    public List<CommentOutboxMessage> lockPendingBatch(int batchSize) {
        return jdbcTemplate.query("""
                SELECT event_id, aggregate_id, event_type, topic, payload::text, attempts
                FROM comment_outbox
                WHERE status = 'PENDING' AND next_attempt_at <= NOW()
                ORDER BY created_at
                LIMIT ?
                FOR UPDATE SKIP LOCKED
                """, (rs, rowNum) -> new CommentOutboxMessage(
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
                UPDATE comment_outbox
                SET status = 'PUBLISHED', published_at = NOW(), last_error = NULL
                WHERE event_id = ? AND status = 'PENDING'
                """, eventId);
    }

    public void markFailed(UUID eventId, String error, Duration retryDelay) {
        String safeError = error == null ? "Unknown Kafka publish failure"
                : error.substring(0, Math.min(error.length(), 2000));
        jdbcTemplate.update("""
                UPDATE comment_outbox
                SET attempts = attempts + 1, last_error = ?, next_attempt_at = ?
                WHERE event_id = ? AND status = 'PENDING'
                """, safeError, Timestamp.from(Instant.now().plus(retryDelay)), eventId);
    }

    public int deletePublishedBefore(Instant threshold) {
        return jdbcTemplate.update("""
                DELETE FROM comment_outbox
                WHERE status = 'PUBLISHED' AND published_at < ?
                """, Timestamp.from(threshold));
    }
}
