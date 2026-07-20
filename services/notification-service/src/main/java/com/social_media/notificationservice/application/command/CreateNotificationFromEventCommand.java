package com.social_media.notificationservice.application.command;

import com.social_media.notificationservice.domain.model.enums.NotificationType;
import com.social_media.notificationservice.domain.model.enums.TargetType;

public record CreateNotificationFromEventCommand(String sourceEventId,
                                                 String recipientId,
                                                 String actorId,
                                                 NotificationType notificationType,
                                                 TargetType targetType,
                                                 String targetId,
                                                 String message) {
}
