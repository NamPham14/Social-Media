package com.social_media.notificationservice.domain.model.aggregate;

import com.social_media.notificationservice.domain.model.enums.NotificationType;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Notification {
    private final Long id;
    private final String recipientId;
    private final String actorId;
    private final NotificationType notificationType;
    private final TargetType targetType;
    private final String targetId;
    private final String sourceEventId;
    private final String message;
    private Boolean isRead;
    private final LocalDateTime createdAt;
    private LocalDateTime readAt;

    private Notification(
            Long id,
            String recipientId,
            String actorId,
            NotificationType notificationType,
            TargetType targetType,
            String targetId,
            String sourceEventId,
            String message,
            Boolean isRead,
            LocalDateTime createdAt,
            LocalDateTime readAt
    ) {
        this.id = id;
        this.recipientId = recipientId;
        this.actorId = actorId;
        this.notificationType = notificationType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.sourceEventId = sourceEventId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public static Notification createFromEvent(
            String sourceEventId,
            String recipientId,
            String actorId,
            NotificationType notificationType,
            TargetType targetType,
            String targetId,
            String message
    ) {
        validateCreate(sourceEventId, recipientId, actorId, notificationType, targetType, targetId, message);
        validateNotSelfAction(recipientId, actorId);

        return new Notification(
                null,
                recipientId,
                actorId,
                notificationType,
                targetType,
                targetId,
                sourceEventId,
                message,
                false,
                LocalDateTime.now(),
                null
        );
    }

    public static Notification restore(
            Long id,
            String recipientId,
            String actorId,
            NotificationType notificationType,
            TargetType targetType,
            String targetId,
            String sourceEventId,
            String message,
            Boolean isRead,
            LocalDateTime createdAt,
            LocalDateTime readAt
    ) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }

        validateCreate(sourceEventId, recipientId, actorId, notificationType, targetType, targetId, message);
        validateReadState(isRead, createdAt, readAt);

        return new Notification(
                id,
                recipientId,
                actorId,
                notificationType,
                targetType,
                targetId,
                sourceEventId,
                message,
                isRead,
                createdAt,
                readAt
        );
    }

    public void markAsRead(String currentUserId) {
        validateOwner(currentUserId);

        if (isRead()) {
            return;
        }

        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsUnread(String currentUserId) {
        validateOwner(currentUserId);

        if (isUnread()) {
            return;
        }

        this.isRead = false;
        this.readAt = null;
    }

    public boolean isUnread() {
        return Boolean.FALSE.equals(this.isRead);
    }

    public boolean isRead() {
        return Boolean.TRUE.equals(this.isRead);
    }

    public boolean belongsTo(String currentUserId) {
        return currentUserId != null && this.recipientId.equals(currentUserId);
    }

    private void validateOwner(String currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("currentUserId is required");
        }

        if (!belongsTo(currentUserId)) {
            throw new IllegalStateException("User cannot update another user's notification");
        }
    }

    private static void validateCreate(
            String sourceEventId,
            String recipientId,
            String actorId,
            NotificationType notificationType,
            TargetType targetType,
            String targetId,
            String message
    ) {
        requireText(sourceEventId, "sourceEventId");
        requireNonNull(recipientId, "recipientId");
        requireNonNull(actorId, "actorId");
        requireNonNull(notificationType, "notificationType");
        requireNonNull(targetType, "targetType");
        requireNonNull(targetId, "targetId");
        requireText(message, "message");
    }

    private static void validateNotSelfAction(String recipientId, String actorId) {
        if (recipientId.equals(actorId)) {
            throw new IllegalArgumentException("Cannot create notification for self action");
        }
    }

    private static void validateReadState(Boolean isRead, LocalDateTime createdAt, LocalDateTime readAt) {
        requireNonNull(isRead, "isRead");
        requireNonNull(createdAt, "createdAt");

        if (Boolean.FALSE.equals(isRead) && readAt != null) {
            throw new IllegalArgumentException("Unread notification cannot have readAt");
        }
    }

    private static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}
