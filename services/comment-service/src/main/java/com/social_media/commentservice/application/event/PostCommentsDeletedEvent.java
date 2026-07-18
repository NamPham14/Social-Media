package com.social_media.commentservice.application.event;

import java.util.List;
import java.util.UUID;

public record PostCommentsDeletedEvent(String id, UUID postId, List<UUID> commentIds) {
    public PostCommentsDeletedEvent {
        commentIds = List.copyOf(commentIds);
    }
}
