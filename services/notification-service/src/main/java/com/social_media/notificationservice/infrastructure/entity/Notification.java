package com.social_media.notificationservice.infrastructure.entity;

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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * User nhận thông báo.
     */
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    /*
     * User tạo ra hành động.
     */
    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    /*
     * Loại thông báo: POST_LIKED, POST_COMMENTED, USER_FOLLOWED,...
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    /*
     * Đối tượng mà notification trỏ tới: POST, COMMENT, USER.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    private TargetType targetType;

    /*
     * ID của đối tượng liên quan.
     */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /*
     * ID event nhận từ Kafka/RabbitMQ.
     * Dùng để chống tạo notification trùng.
     */
    @Column(name = "source_event_id", nullable = false, unique = true, length = 100)
    private String sourceEventId;

    /*
     * Nội dung hiển thị trên chuông app.
     */
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    /*
     * false = chưa đọc
     * true = đã đọc
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}