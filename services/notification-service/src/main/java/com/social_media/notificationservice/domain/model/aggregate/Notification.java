package com.social_media.notificationservice.domain.model.aggregate;

import com.social_media.notificationservice.domain.model.enums.NotificationType;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notifications_recipient_created_at",
                        columnList = "recipient_id, created_at"
                ),
                @Index(
                        name = "idx_notifications_recipient_is_read",
                        columnList = "recipient_id, is_read"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notifications_source_event_id",
                        columnNames = "source_event_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * User nhận notification.
     * Ví dụ:
     * - Chủ bài post
     * - Chủ comment
     * - Người được follow
     */
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    /*
     * User tạo ra hành động.
     * Ví dụ:
     * - Người like
     * - Người comment
     * - Người follow
     */
    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    /*
     * Loại notification.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    /*
     * Loại đối tượng mà notification trỏ tới.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    private TargetType targetType;

    /*
     * ID của đối tượng liên quan.
     *
     * Nếu targetType = POST    => targetId là postId
     * Nếu targetType = COMMENT => targetId là commentId
     * Nếu targetType = USER    => targetId là userId
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /*
     * ID event từ Kafka/RabbitMQ.
     * Dùng để chống tạo notification trùng khi broker gửi lại message.
     */
    @Column(name = "source_event_id", nullable = false, unique = true, length = 100)
    private String sourceEventId;

    /*
     * Nội dung hiện trên chuông app.
     * Ví dụ: "Nam liked your post."
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /*
     * false = chưa đọc
     * true  = đã đọc
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /*
     * Thời điểm notification được đọc.
     * Field này không bắt buộc, nhưng nên có để biết user đọc lúc nào.
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    // =====================================================
    // FACTORY METHOD
    // =====================================================

    public static Notification createFromEvent(
            String sourceEventId,
            Long recipientId,
            Long actorId,
            NotificationType notificationType,
            TargetType targetType,
            Long targetId,
            String message
    ) {
        validateCreate(
                sourceEventId,
                recipientId,
                actorId,
                notificationType,
                targetType,
                targetId,
                message
        );

        if (recipientId.equals(actorId)) {
            throw new IllegalArgumentException("Cannot create notification for self action");
        }

        return Notification.builder()
                .sourceEventId(sourceEventId)
                .recipientId(recipientId)
                .actorId(actorId)
                .notificationType(notificationType)
                .targetType(targetType)
                .targetId(targetId)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .readAt(null)
                .build();
    }

    // =====================================================
    // AGGREGATE BEHAVIORS
    // =====================================================

    public void markAsRead(Long currentUserId) {
        validateOwner(currentUserId);

        if (Boolean.TRUE.equals(this.isRead)) {
            return;
        }

        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsUnread(Long currentUserId) {
        validateOwner(currentUserId);

        if (Boolean.FALSE.equals(this.isRead)) {
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

    public boolean belongsTo(Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }

        return this.recipientId.equals(currentUserId);
    }

    private void validateOwner(Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("currentUserId is required");
        }

        if (!this.recipientId.equals(currentUserId)) {
            throw new IllegalStateException("User cannot update another user's notification");
        }
    }

    private static void validateCreate(
            String sourceEventId,
            Long recipientId,
            Long actorId,
            NotificationType notificationType,
            TargetType targetType,
            Long targetId,
            String message
    ) {
        if (sourceEventId == null || sourceEventId.isBlank()) {
            throw new IllegalArgumentException("sourceEventId is required");
        }

        if (recipientId == null) {
            throw new IllegalArgumentException("recipientId is required");
        }

        if (actorId == null) {
            throw new IllegalArgumentException("actorId is required");
        }

        if (notificationType == null) {
            throw new IllegalArgumentException("notificationType is required");
        }

        if (targetType == null) {
            throw new IllegalArgumentException("targetType is required");
        }

        if (targetId == null) {
            throw new IllegalArgumentException("targetId is required");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
    }
}
