package com.social_media.commentservice.infrastructure.messaging.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@ConditionalOnProperty(prefix = "messaging.outbox", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class CommentOutboxRelay {

    private final CommentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${messaging.outbox.batch-size:100}")
    private int batchSize;

    @Value("${messaging.outbox.retry-delay-ms:5000}")
    private long retryDelayMs;

    @Value("${messaging.outbox.publish-timeout-seconds:10}")
    private long publishTimeoutSeconds;

    @Value("${messaging.outbox.retention-days:7}")
    private long retentionDays;

    @Scheduled(fixedDelayString = "${messaging.outbox.poll-delay-ms:1000}")
    @Transactional
    public void relayPendingEvents() {
        for (CommentOutboxMessage message : outboxRepository.lockPendingBatch(batchSize)) {
            try {
                kafkaTemplate.send(message.topic(), message.aggregateId().toString(), message.payload())
                        .get(publishTimeoutSeconds, TimeUnit.SECONDS);
                outboxRepository.markPublished(message.eventId());
                log.info("Published comment outbox eventId={} eventType={} topic={} aggregateId={} attempts={}",
                        message.eventId(), message.eventType(), message.topic(), message.aggregateId(),
                        message.attempts());
            } catch (InterruptedException failure) {
                Thread.currentThread().interrupt();
                outboxRepository.markFailed(message.eventId(), failure.getMessage(), Duration.ofMillis(retryDelayMs));
                return;
            } catch (ExecutionException | TimeoutException failure) {
                outboxRepository.markFailed(message.eventId(), failure.getMessage(), Duration.ofMillis(retryDelayMs));
                log.warn("Failed to publish comment outbox eventId={} eventType={} topic={} attempt={}",
                        message.eventId(), message.eventType(), message.topic(), message.attempts() + 1, failure);
            }
        }
    }

    @Scheduled(cron = "${messaging.outbox.cleanup-cron:0 0 3 * * *}")
    @Transactional
    public void purgePublishedEvents() {
        int deleted = outboxRepository.deletePublishedBefore(
                Instant.now().minus(retentionDays, ChronoUnit.DAYS));
        if (deleted > 0) {
            log.info("Purged {} published comment outbox events", deleted);
        }
    }
}
