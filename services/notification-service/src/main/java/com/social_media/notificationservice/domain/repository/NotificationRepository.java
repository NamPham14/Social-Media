package com.social_media.notificationservice.domain.repository;

import com.social_media.notificationservice.domain.model.aggregate.Notification;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import com.social_media.notificationservice.domain.model.enums.NotificationType;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    Optional<Notification> findByIdAndRecipientId(Long id, String recipientId);

    Optional<Notification> findBySourceEventId(String sourceEventId);

    boolean existsBySourceEventId(String sourceEventId);

    List<Notification> findByRecipientId(String recipientId, int limit);

    List<Notification> findUnreadByRecipientId(String recipientId, int limit);

    long countUnreadByRecipientId(String recipientId);

    void delete(Notification notification);

    void deleteByTargetTypeAndTargetId(TargetType targetType, String targetId);

    void deleteByRecipientIdAndActorIdAndNotificationTypeAndTargetTypeAndTargetId(
            String recipientId,
            String actorId,
            NotificationType notificationType,
            TargetType targetType,
            String targetId
    );
}


