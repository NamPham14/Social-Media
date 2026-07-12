package com.social_media.postservice.application.ports.output;

import com.social_media.postservice.application.dto.events.PostCreatedIntegrationEvent;

public interface PostEventPublisher {
    void publishPostCreated(PostCreatedIntegrationEvent event);
}
