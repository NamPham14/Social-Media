package com.social_media.followerservice.infrastructure.messaging;

import com.social_media.followerservice.domain.model.outbox.Outbox;
import com.social_media.followerservice.domain.model.outbox.OutboxStatus;
import com.social_media.followerservice.domain.repository.OutBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutBoxPublisher {

    private final OutBoxRepository outBoxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Duration SEND_TIMEOUT = Duration.ofSeconds(15);

    @Scheduled(fixedRate = 20000)
    @Transactional
    public void publish() {
        List<Outbox> outboxes = outBoxRepository.findByStatus(OutboxStatus.NEW);
        for (Outbox outbox : outboxes) {
            try {
                kafkaTemplate.send(outbox.getTopic(), outbox.getPayload())
                        .get(SEND_TIMEOUT.getSeconds(), TimeUnit.SECONDS);

                outbox.setStatus(OutboxStatus.SENT);
                outBoxRepository.save(outbox);
                log.info("Outbox event sent and marked SENT: id={}, topic={}", outbox.getId(), outbox.getTopic());
            } catch (Exception e) {
                log.error("Failed to send outbox event: id={}, topic={}, error={}",
                        outbox.getId(), outbox.getTopic(), e.getMessage());
            }
        }
    }
}
