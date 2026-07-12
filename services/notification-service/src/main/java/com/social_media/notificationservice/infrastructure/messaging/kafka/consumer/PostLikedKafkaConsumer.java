package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.notificationservice.application.mapper.NotificationCommandMapper;
import com.social_media.notificationservice.application.usecase.CreateNotificationFromEventUseCase;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikedKafkaConsumer {

    private final NotificationCommandMapper mapper;
    private final CreateNotificationFromEventUseCase useCase;

    @KafkaListener(topics = "post-liked-topic", groupId = "notification-service")
    public void consume(PostLikedEvent event) {
        useCase.handle(mapper.fromPostLikedEvent(event));
    }
}