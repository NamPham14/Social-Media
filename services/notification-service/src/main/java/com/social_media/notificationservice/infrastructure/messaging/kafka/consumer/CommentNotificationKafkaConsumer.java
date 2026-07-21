package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.notificationservice.application.mapper.NotificationCommandMapper;
import com.social_media.notificationservice.application.usecase.CreateNotificationFromEventUseCase;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.CommentNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentNotificationKafkaConsumer {

    private final NotificationCommandMapper mapper;
    private final CreateNotificationFromEventUseCase useCase;

    @KafkaListener(
            topics = {
                    "${messaging.topics.comment-created}",
                    "${messaging.topics.comment-replied}"
            },
            groupId = "notification-service"
    )
    public void consume(CommentNotificationEvent event) {
        useCase.handle(mapper.fromCommentNotificationEvent(event));
    }
}
