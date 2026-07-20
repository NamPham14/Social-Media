package com.social_media.postservice.application.dto.events;

import lombok.Value;

@Value
public class PostDeleteIntegrationEvent {
    String id;
    String postId;
    String authorId;

}
