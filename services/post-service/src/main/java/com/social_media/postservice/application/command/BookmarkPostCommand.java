package com.social_media.postservice.application.command;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.domain.exception.ErrorCode;
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
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
