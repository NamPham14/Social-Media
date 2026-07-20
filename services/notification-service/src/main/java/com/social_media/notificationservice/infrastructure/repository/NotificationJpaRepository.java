package com.social_media.notificationservice.infrastructure.repository;

import com.social_media.notificationservice.infrastructure.entity.NotificationEntity;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import com.social_media.notificationservice.domain.model.enums.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    Optional<NotificationEntity> findByIdAndRecipientId(Long id, String recipientId);

    Optional<NotificationEntity> findBySourceEventId(String sourceEventId);

    boolean existsBySourceEventId(String sourceEventId);

    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(String recipientId, Pageable pageable);

    List<NotificationEntity> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(String recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalse(String recipientId);

    void deleteByTargetTypeAndTargetId(TargetType targetType, String targetId);

    void deleteByRecipientIdAndActorIdAndNotificationTypeAndTargetTypeAndTargetId(
            String recipientId,
            String actorId,
            NotificationType notificationType,
            TargetType targetType,
            String targetId
    );
}


