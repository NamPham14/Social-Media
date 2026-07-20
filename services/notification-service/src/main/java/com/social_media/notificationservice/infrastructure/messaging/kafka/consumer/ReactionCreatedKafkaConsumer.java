package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.notificationservice.application.mapper.NotificationCommandMapper;
import com.social_media.notificationservice.application.usecase.CreateNotificationFromEventUseCase;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.ReactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactionCreatedKafkaConsumer {

    private final NotificationCommandMapper mapper;
    private final CreateNotificationFromEventUseCase useCase;

    @KafkaListener(topics = "${messaging.topics.reaction-created}", groupId = "notification-service")
    public void consume(ReactionCreatedEvent event) {
        useCase.handle(mapper.fromReactionCreatedEvent(event));
    }
}
