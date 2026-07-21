package com.social_media.identityservice.domain.repository;

public interface OutboxEventRepository {
    void save(String aggregateId, String eventType, String payload);
}
