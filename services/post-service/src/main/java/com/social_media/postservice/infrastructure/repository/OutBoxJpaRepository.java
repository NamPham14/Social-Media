package com.social_media.postservice.infrastructure.repository;

import com.social_media.postservice.domain.model.outbox.OutBox;
import com.social_media.postservice.domain.model.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutBoxJpaRepository extends JpaRepository<OutBox, UUID> {
    List<OutBox> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
