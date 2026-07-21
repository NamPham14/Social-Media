package com.social_media.interactionservice.application.port.out;

import com.social_media.interactionservice.application.event.ReactionNotificationEvent;

public interface InteractionEventOutbox {
    void append(ReactionNotificationEvent event);
}
