package com.social_media.commentservice.infrastructure.messaging.outbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentOutboxRelayTest {

    @Mock
    private CommentOutboxRepository outboxRepository;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private CommentOutboxRelay relay;

    @BeforeEach
    void setUp() {
        relay = new CommentOutboxRelay(outboxRepository, kafkaTemplate);
        ReflectionTestUtils.setField(relay, "topic", "post-comments-deleted-topic");
        ReflectionTestUtils.setField(relay, "batchSize", 100);
        ReflectionTestUtils.setField(relay, "retryDelayMs", 5000L);
        ReflectionTestUtils.setField(relay, "publishTimeoutSeconds", 10L);
        ReflectionTestUtils.setField(relay, "retentionDays", 7L);
    }

    @Test
    void marksMessagePublishedAfterKafkaAcknowledgesIt() {
        UUID eventId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        CommentOutboxMessage message = new CommentOutboxMessage(eventId, postId, "{}", 0);
        when(outboxRepository.lockPendingBatch(100)).thenReturn(List.of(message));
        when(kafkaTemplate.send("post-comments-deleted-topic", postId.toString(), "{}"))
                .thenReturn(CompletableFuture.completedFuture(null));

        relay.relayPendingEvents();

        verify(outboxRepository).markPublished(eventId);
        verify(outboxRepository, never()).markFailed(eq(eventId), anyString(), any(Duration.class));
    }

    @Test
    void schedulesAnotherAttemptWhenKafkaRejectsTheMessage() {
        UUID eventId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        CommentOutboxMessage message = new CommentOutboxMessage(eventId, postId, "{}", 2);
        CompletableFuture<org.springframework.kafka.support.SendResult<String, String>> failed =
                new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("broker unavailable"));
        when(outboxRepository.lockPendingBatch(100)).thenReturn(List.of(message));
        when(kafkaTemplate.send("post-comments-deleted-topic", postId.toString(), "{}"))
                .thenReturn(failed);

        relay.relayPendingEvents();

        verify(outboxRepository).markFailed(eq(eventId), anyString(), eq(Duration.ofSeconds(5)));
        verify(outboxRepository, never()).markPublished(eventId);
    }
}
