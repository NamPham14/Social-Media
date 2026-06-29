package com.social_media.followerservice.infrastructure.messaging;

import com.social_media.followerservice.domain.event.UserFollowedEvent;
import com.social_media.followerservice.domain.event.UserUnfollowedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LocalFollowEventListener {

    private final KafkaEventProducer kafkaEventProducer;

    public LocalFollowEventListener(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowEvent(UserFollowedEvent event) {
        kafkaEventProducer.publishUserFollowed(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUnfollowEvent(UserUnfollowedEvent event) {
        kafkaEventProducer.publishUserUnfollowed(event);
    }
}
