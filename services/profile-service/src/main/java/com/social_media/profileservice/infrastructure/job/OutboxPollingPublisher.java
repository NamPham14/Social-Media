package com.social_media.profileservice.infrastructure.job;

import com.social_media.profileservice.infrastructure.persistence.entity.OutboxEventEntity;
import com.social_media.profileservice.infrastructure.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPollingPublisher {

    private final OutboxEventJpaRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEventEntity> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc("PENDING");

        for (OutboxEventEntity event : pendingEvents) {
            try {
                // Đẩy thông báo đổi Avatar lên topic profile-events
                kafkaTemplate.send("profile-events", event.getAggregateId(), event.getPayload());

                event.setStatus("COMPLETED");
                outboxRepository.save(event);
                log.info("🚀 Đã gửi thành công sự kiện {} của user {}", event.getEventType(), event.getAggregateId());
            } catch (Exception e) {
                log.error("❌ Gửi sự kiện thất bại, sẽ thử lại sau...", e);
            }
        }
    }
}
