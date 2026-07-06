package com.social_media.followerservice.infrastructure.messaging;

import com.social_media.followerservice.application.port.FollowEventPublisher;
import com.social_media.followerservice.domain.event.UserFollowedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer implements FollowEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(UserFollowedEvent event) {
        kafkaTemplate.send("user-followed-topic", event);
    }
}
