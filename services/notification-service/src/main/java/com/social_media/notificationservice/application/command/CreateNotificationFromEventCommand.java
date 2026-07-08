package com.social_media.notificationservice.application.command;

import com.social_media.notificationservice.domain.model.enums.NotificationType;
import com.social_media.notificationservice.domain.model.enums.TargetType;

public record CreateNotificationFromEventCommand(String sourceEventId,
                                                 Long recipientId,
                                                 Long actorId,
                                                 NotificationType notificationType,
                                                 TargetType targetType,
                                                 Long targetId,
                                                 String message) {
}
