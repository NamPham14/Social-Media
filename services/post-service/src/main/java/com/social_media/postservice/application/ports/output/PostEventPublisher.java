package com.social_media.postservice.application.ports.output;

import com.social_media.postservice.application.dto.events.PostCreatedIntegrationEvent;
import com.social_media.postservice.application.dto.events.PostDeleteIntegrationEvent;

public interface PostEventPublisher {
    void publishPostCreated(PostCreatedIntegrationEvent event);
    void publishPostDelete(PostDeleteIntegrationEvent event);
}
