package com.social_media.followerservice.domain.repository;

import com.social_media.followerservice.domain.model.outbox.Outbox;
import com.social_media.followerservice.domain.model.outbox.OutboxStatus;

import java.util.List;
import java.util.UUID;

public interface OutBoxRepository {
    Outbox findById(UUID id);
    List<Outbox> findAll();
    Outbox save(Outbox outbox);
    Outbox update(Outbox outbox);
    void delete(UUID uuid);
    List<Outbox> findByStatus(OutboxStatus status);
}
