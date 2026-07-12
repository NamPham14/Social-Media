package com.social_media.postservice.application.dto.events;


import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class PostCreatedIntegrationEvent {
    String id;
    String postId;
    String authorId;
    String caption;
    LocalDateTime createdAt;
}
