package com.social_media.commentservice.application.port.out;

import java.util.UUID;

public interface PostAvailabilityPort {
    AvailablePost getCommentable(UUID postId, UUID actorId);

    record AvailablePost(UUID postId, UUID ownerId) {
    }
}
