package com.social_media.profileservice.domain.repository;

public interface OutboxEventRepository {
    void save(String aggregateId, String eventType, String payload);
}
