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

    public void publishUserFollowed(UserFollowedEvent event) {
        kafkaTemplate.send("user.followed", event);
    }

    public void publishUserUnfollowed(com.social_media.followerservice.domain.event.UserUnfollowedEvent event) {
        kafkaTemplate.send("user.unfollowed", event);
    }
}
