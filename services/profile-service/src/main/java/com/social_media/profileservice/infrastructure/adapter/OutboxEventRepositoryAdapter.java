package com.social_media.profileservice.infrastructure.adapter;

import com.social_media.profileservice.domain.repository.OutboxEventRepository;
import com.social_media.profileservice.infrastructure.persistence.entity.OutboxEventEntity;
import com.social_media.profileservice.infrastructure.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final OutboxEventJpaRepository outboxJpaRepository;

    @Override
    public void save(String aggregateId, String eventType, String payload) {
        OutboxEventEntity entity = OutboxEventEntity.builder()
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .status("PENDING")
                .build();
        outboxJpaRepository.save(entity);
    }
}
