package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.notificationservice.application.mapper.NotificationCommandMapper;
import com.social_media.notificationservice.application.usecase.CreateNotificationFromEventUseCase;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.UserFollowedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFollowedKafkaConsumer {

    private final NotificationCommandMapper mapper;
    private final CreateNotificationFromEventUseCase useCase;

    @KafkaListener(topics = "user-followed-topic", groupId = "notification-service")
    public void consume(UserFollowedEvent event) {
        useCase.handle(mapper.fromUserFollowedEvent(event));
    }
}