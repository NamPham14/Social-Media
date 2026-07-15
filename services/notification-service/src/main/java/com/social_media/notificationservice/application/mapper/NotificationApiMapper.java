package com.social_media.notificationservice.application.mapper;

import com.social_media.notificationservice.api.dto.response.NotificationResponse;
import com.social_media.notificationservice.domain.model.aggregate.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationApiMapper {

    public NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getActorId(),
                notification.getNotificationType().name(),
                notification.getTargetType().name(),
                notification.getTargetId(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}
