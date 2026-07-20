package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.notificationservice.application.usecase.DeleteFollowNotificationUseCase;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.UserUnfollowedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUnfollowedKafkaConsumer {

    private final DeleteFollowNotificationUseCase useCase;

    @KafkaListener(topics = "user-unfollowed-topic", groupId = "notification-service")
    public void consume(UserUnfollowedEvent event) {
        useCase.handle(event.followerId(), event.followingId());
    }
}
