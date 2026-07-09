package com.social_media.postservice.application.command;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class BookmarkPostCommand {
    UUID userId;
    UUID postId;

    public BookmarkPostCommand(UUID userId, UUID postId) {
        this.userId = userId;
        this.postId = postId;
        validate();
    }

    private void validate() {
        if (userId == null || postId == null) {
            throw new com.social_media.postservice.application.exception.ResourceNotFoundException();
        }
    }
}
