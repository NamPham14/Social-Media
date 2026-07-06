package com.social_media.followerservice.application.port;

import com.social_media.followerservice.domain.event.UserFollowedEvent;

public interface FollowEventPublisher {
    void publish(UserFollowedEvent event);
}
