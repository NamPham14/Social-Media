package com.social_media.followerservice.infrastructure.persistence.repository;

import com.social_media.followerservice.domain.model.outbox.Outbox;
import com.social_media.followerservice.domain.model.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutBoxJpaRepository extends JpaRepository<Outbox , UUID> {
    List<Outbox> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
