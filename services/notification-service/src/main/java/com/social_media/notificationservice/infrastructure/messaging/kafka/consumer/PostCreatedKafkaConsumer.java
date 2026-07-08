package com.social_media.notificationservice.infrastructure.messaging.kafka.consumer;

import com.social_media.common.api.ApiResponse;
import com.social_media.notificationservice.application.command.CreateNotificationFromEventCommand;
import com.social_media.notificationservice.application.usecase.CreateNotificationFromEventUseCase;
import com.social_media.notificationservice.domain.model.enums.NotificationType;
import com.social_media.notificationservice.domain.model.enums.TargetType;
import com.social_media.notificationservice.infrastructure.client.post.PostClient;
import com.social_media.notificationservice.infrastructure.client.post.dto.PostClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostCreatedKafkaConsumer {

    private final PostClient postClient;
    private final CreateNotificationFromEventUseCase useCase;

    @KafkaListener(topics = "post-created-topic", groupId = "notification-service")
    public void consume(String postId) {
        ApiResponse<PostClientResponse> response =
                postClient.getPostById(Long.valueOf(postId));

        PostClientResponse post = response.getData();

        CreateNotificationFromEventCommand command =
                new CreateNotificationFromEventCommand(
                        "post-created-" + post.id(),
                        post.userId(),
                        post.userId(),
                        NotificationType.POST_CREATED,
                        TargetType.POST,
                        post.id(),
                        "Đăng bài: " + post.caption()+" thành công."
                );

        useCase.handle(command);
    }
}