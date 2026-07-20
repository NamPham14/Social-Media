package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.notificationservice.application.usecase.DeleteNotificationsByTargetUseCase;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.PostDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostDeletedKafkaConsumer {

    private final DeleteNotificationsByTargetUseCase useCase;

    @KafkaListener(topics = "post-delete", groupId = "notification-service")
    public void consume(PostDeletedEvent event) {
        useCase.handle(TargetType.POST, event.postId());
    }
}

