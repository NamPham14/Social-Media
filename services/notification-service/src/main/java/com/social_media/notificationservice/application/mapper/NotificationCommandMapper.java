package com.social_media.notificationservice.application.mapper;

import com.social_media.notificationservice.application.command.CreateNotificationFromEventCommand;
import com.social_media.notificationservice.domain.model.enums.NotificationType;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.CommentNotificationEvent;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.PostCreatedEvent;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.PostLikedEvent;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.ReactionCreatedEvent;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.UserFollowedEvent;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommandMapper {

    public CreateNotificationFromEventCommand fromPostLikedEvent(PostLikedEvent event) {
        return new CreateNotificationFromEventCommand(
                event.eventId(),
                event.postOwnerId(),
                event.actorId(),
                NotificationType.POST_LIKED,
                TargetType.POST,
                event.postId(),
                event.actorName() + " liked your post"
        );
    }

    public CreateNotificationFromEventCommand fromUserFollowedEvent(UserFollowedEvent event) {
        return new CreateNotificationFromEventCommand(
                event.eventId(),
                event.followingId(),
                event.followerId(),
                NotificationType.USER_FOLLOWED,
                TargetType.USER,
                event.followerId(),
                event.followerName() + " followed you"
        );
    }

    public CreateNotificationFromEventCommand fromPostCreatedEvent(PostCreatedEvent event) {
        return new CreateNotificationFromEventCommand(
                event.id(),
                event.authorId(),
                event.authorId(),
                NotificationType.POST_CREATED,
                TargetType.POST,
                event.postId(),
                "Đăng bài: " + event.caption() + " thành công."
        );
    }

    public CreateNotificationFromEventCommand fromReactionCreatedEvent(ReactionCreatedEvent event) {
        TargetType targetType = TargetType.valueOf(event.targetType());
        NotificationType notificationType = switch (targetType) {
            case POST -> NotificationType.POST_LIKED;
            case COMMENT -> NotificationType.COMMENT_LIKED;
            case USER -> throw new IllegalArgumentException("User target reactions are not supported");
        };

        String targetName = targetType == TargetType.POST ? "bài viết" : "bình luận";
        return new CreateNotificationFromEventCommand(
                event.eventId(),
                event.recipientId(),
                event.actorId(),
                notificationType,
                targetType,
                event.targetId(),
                "Có người đã " + event.reactionType().toLowerCase() + " " + targetName + " của bạn"
        );
    }

    public CreateNotificationFromEventCommand fromCommentNotificationEvent(CommentNotificationEvent event) {
        NotificationType notificationType = switch (event.eventType()) {
            case "CommentCreatedV1" -> NotificationType.POST_COMMENTED;
            case "CommentRepliedV1" -> NotificationType.COMMENT_REPLIED;
            default -> throw new IllegalArgumentException("Unsupported comment event type: " + event.eventType());
        };
        TargetType targetType = notificationType == NotificationType.POST_COMMENTED ? TargetType.POST : TargetType.COMMENT;
        String targetId = notificationType == NotificationType.POST_COMMENTED ? event.postId() : event.parentCommentId();
        String message = notificationType == NotificationType.POST_COMMENTED
                ? "Có người đã bình luận bài viết của bạn"
                : "Có người đã trả lời bình luận của bạn";

        return new CreateNotificationFromEventCommand(
                event.eventId(),
                event.recipientId(),
                event.actorId(),
                notificationType,
                targetType,
                targetId,
                message
        );
    }
}
