package com.social_media.followerservice.infrastructure.messaging;

import com.social_media.followerservice.application.dto.events.UserFollowedEvent;
import com.social_media.followerservice.application.dto.events.UserUnfollowedEvent;
import com.social_media.followerservice.application.port.FollowEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventProducer implements FollowEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(UserFollowedEvent event) {
        kafkaTemplate.send("user-followed-topic", event)
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("UserFollowedEvent sent successfully: {}", event.eventId());
                    } else {
                        log.error("Failed to send UserFollowedEvent: {}", ex.getMessage());
                    }
                });
    }

    public void publishUnfollowed(UserUnfollowedEvent event) {
        kafkaTemplate.send("user-unfollowed-topic", event)
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("UserUnfollowedEvent sent successfully: {}", event.eventId());
                    } else {
                        log.error("Failed to send UserUnfollowedEvent: {}", ex.getMessage());
                    }
                });
    }
}
