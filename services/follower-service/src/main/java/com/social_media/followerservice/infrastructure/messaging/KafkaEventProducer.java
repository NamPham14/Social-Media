package com.social_media.followerservice.infrastructure.messaging;

import com.social_media.followerservice.domain.event.UserFollowedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserFollowedKafkaEvent(UserFollowedEvent event) {
        kafkaTemplate.send("user-followed-topic", event);
    }
}
