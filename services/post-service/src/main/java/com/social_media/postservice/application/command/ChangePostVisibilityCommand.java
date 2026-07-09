package com.social_media.postservice.application.command;

import com.social_media.postservice.domain.model.post.valueobject.PostStatus;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ChangePostVisibilityCommand {
    UUID postId;
    UUID userId;
    PostStatus newStatus;

    public ChangePostVisibilityCommand(UUID postId, UUID userId, PostStatus newStatus) {
        this.postId = postId;
        this.userId = userId;
        this.newStatus = newStatus;
        validate();
    }

    private void validate() {
        if (postId == null || userId == null || newStatus == null) {
            throw new com.social_media.postservice.application.exception.ResourceNotFoundException();
        }
    }
}
