package com.social_media.notificationservice.infrastructure.mapper;

import com.social_media.notificationservice.domain.model.aggregate.Notification;
import com.social_media.notificationservice.infrastructure.entity.NotificationEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {
    public Notification toDomain(NotificationEntity entity) {
        return Notification.restore(
                entity.getId(),
                entity.getRecipientId(),
                entity.getActorId(),
                entity.getNotificationType(),
                entity.getTargetType(),
                entity.getTargetId(),
                entity.getSourceEventId(),
                entity.getMessage(),
                entity.getIsRead(),
                entity.getCreatedAt(),
                entity.getReadAt()
        );
    }

    public NotificationEntity toEntity(Notification notification) {
        return NotificationEntity.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientId())
                .actorId(notification.getActorId())
                .notificationType(notification.getNotificationType())
                .targetType(notification.getTargetType())
                .targetId(notification.getTargetId())
                .sourceEventId(notification.getSourceEventId())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
