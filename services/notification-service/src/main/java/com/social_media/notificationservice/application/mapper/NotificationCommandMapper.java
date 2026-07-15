package com.social_media.notificationservice.application.mapper;

import com.social_media.notificationservice.application.command.CreateNotificationFromEventCommand;
import com.social_media.notificationservice.domain.model.enums.NotificationType;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.PostCreatedEvent;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.PostLikedEvent;
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
                event.followingUserId(),
                event.followerUserId(),
                NotificationType.USER_FOLLOWED,
                TargetType.USER,
                event.followerUserId(),
                event.followerName() + " followed you"
        );
    }

    public CreateNotificationFromEventCommand fromPostCreatedEvent(PostCreatedEvent event) {
        return new CreateNotificationFromEventCommand(
                event.eventId(),                    // sourceEventId
                event.authorId(),                   // recipientId
                event.authorId(),                   // actorId
                NotificationType.POST_CREATED,      // notificationType
                TargetType.POST,                    // targetType
                event.postId(),                     // targetId
                event.authorName() + " created a new post: " + event.caption()
        );
    }
}
