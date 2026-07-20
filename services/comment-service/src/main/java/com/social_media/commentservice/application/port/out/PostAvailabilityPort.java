package com.social_media.commentservice.application.port.out;

import java.util.UUID;

public interface PostAvailabilityPort {
    void ensureCommentable(UUID postId, UUID actorId);
}
