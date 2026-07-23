package com.social_media.identityservice.infrastructure.job;


import com.social_media.identityservice.infrastructure.persistence.entity.OutboxEventEntity;
import com.social_media.identityservice.infrastructure.persistence.repository.OutboxEventJpaRepository;
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

    // Con robot này sẽ thức dậy sau mỗi 5 giây
    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        // Lấy tất cả thư đang nghẽn (PENDING)
        List<OutboxEventEntity> pendingEvents = outboxRepository.findByStatusOrderByCreatedAtAsc("PENDING");

        for (OutboxEventEntity event : pendingEvents) {
            try {
                // Topic được đặt tên là "user-events" (Profile service sẽ nghe topic này)
                kafkaTemplate.send("user-events", event.getAggregateId(),event.getPayload());

                // Gửi thành công thì đánh dấu là COMPLETED
                event.setStatus("COMPLETED");
                outboxRepository.save(event);
                log.info("Đã gửi thành công sự kiện {} của user {}", event.getEventType(), event.getAggregateId());
            }
            catch (Exception e) {
                // Nếu Kafka sập, ta cứ để nó PENDING, 5 giây sau con robot sẽ tự động bốc lên gửi lại!
                // Đây chính là sức mạnh chống mất dữ liệu của Outbox Pattern!
                log.error("Gửi sự kiện thất bại, sẽ thử lại sau...", e);
            }
        }

    }

}
