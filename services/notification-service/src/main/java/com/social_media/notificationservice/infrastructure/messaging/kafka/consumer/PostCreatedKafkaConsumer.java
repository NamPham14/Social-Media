package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.notificationservice.application.mapper.NotificationCommandMapper;
import com.social_media.notificationservice.application.usecase.CreateNotificationFromEventUseCase;
import com.social_media.notificationservice.infrastructure.client.follower.FollowerClient;
import com.social_media.notificationservice.infrastructure.client.follower.dto.FollowerIdPageResponse;
import com.social_media.notificationservice.infrastructure.messaging.kafka.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCreatedKafkaConsumer {
    private static final int FOLLOWER_PAGE_SIZE = 100;

    private final NotificationCommandMapper mapper;
    private final CreateNotificationFromEventUseCase useCase;
    private final FollowerClient followerClient;

    @KafkaListener(topics = "post-created", groupId = "notification-service")
    public void consume(PostCreatedEvent event) {
        int page = 1;
        boolean hasNext;

        do {
            FollowerIdPageResponse followersPage = followerClient
                    .getFollowerIds(event.authorId(), event.authorId(), page, FOLLOWER_PAGE_SIZE)
                    .getData();

            if (followersPage == null) {
                return;
            }

            createNotifications(event, followersPage.items());
            hasNext = followersPage.hasNext();
            page++;
        } while (hasNext);
    }

    private void createNotifications(PostCreatedEvent event, List<String> followerIds) {
        if (followerIds == null || followerIds.isEmpty()) {
            return;
        }

        followerIds.stream()
                .filter(followerId -> followerId != null && !followerId.isBlank())
                .forEach(followerId -> useCase.handle(mapper.fromPostCreatedEvent(event, followerId)));
    }
}
