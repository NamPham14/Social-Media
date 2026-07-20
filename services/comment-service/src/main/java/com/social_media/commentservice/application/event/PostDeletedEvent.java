package com.social_media.commentservice.application.event;

import java.util.UUID;

public record PostDeletedEvent(String id, UUID postId, UUID authorId) {
}
