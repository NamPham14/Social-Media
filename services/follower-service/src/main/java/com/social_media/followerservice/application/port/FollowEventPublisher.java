package com.social_media.followerservice.application.port;

import com.social_media.followerservice.application.dto.events.UserFollowedEvent;

public interface FollowEventPublisher {
    void publish(UserFollowedEvent event);
}
