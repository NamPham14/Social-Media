package com.social_media.postservice.domain.repository;

import com.social_media.postservice.domain.model.outbox.OutBox;
import com.social_media.postservice.domain.model.outbox.OutboxStatus;

import java.util.List;
import java.util.UUID;

public interface OutBoxRepository {

    OutBox findById(UUID id);

    List<OutBox> findAll();

    OutBox save(OutBox outBox);

    OutBox update(OutBox outBox);

    void delete(UUID uuid);

    List<OutBox> findByStatus(OutboxStatus status);
}
