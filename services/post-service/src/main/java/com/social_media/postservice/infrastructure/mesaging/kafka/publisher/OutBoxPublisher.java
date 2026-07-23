package com.social_media.postservice.infrastructure.mesaging.kafka.publisher;


import com.social_media.postservice.domain.model.outbox.OutBox;
import com.social_media.postservice.domain.model.outbox.OutboxStatus;
import com.social_media.postservice.domain.repository.OutBoxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
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
    private final ObjectMapper objectMapper;

    private static final Duration SEND_TIMEOUT = Duration.ofSeconds(15);

    @Scheduled(fixedRate = 20000)
    @Transactional
    public void publish() {
        List<OutBox> outBoxes = outBoxRepository.findByStatus(OutboxStatus.NEW);
        for (OutBox outBox : outBoxes) {
            try {
                JsonNode payloadNode = objectMapper.readTree(outBox.getPayload());
                kafkaTemplate.send(outBox.getTopic(), payloadNode)
                        .get(SEND_TIMEOUT.getSeconds(), TimeUnit.SECONDS);

                outBox.setStatus(OutboxStatus.SENT);
                outBoxRepository.save(outBox);
                log.info("Outbox event sent and marked SENT: id={}, topic={}", outBox.getId(), outBox.getTopic());
            } catch (Exception e) {
                log.error("Failed to send outbox event: id={}, topic={}, error={}",
                        outBox.getId(), outBox.getTopic(), e.getMessage());
            }
        }
    }

}
