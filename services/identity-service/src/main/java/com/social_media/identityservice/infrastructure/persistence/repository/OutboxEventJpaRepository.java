package com.social_media.identityservice.infrastructure.persistence.repository;

import com.social_media.identityservice.infrastructure.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
    
    // Tìm các bức thư đang chờ gửi (status = PENDING)
    List<OutboxEventEntity> findByStatusOrderByCreatedAtAsc(String status);
}
