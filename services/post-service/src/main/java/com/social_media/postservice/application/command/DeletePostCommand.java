package com.social_media.postservice.application.command;

import com.social_media.common.exception.AppException;
import com.social_media.postservice.domain.exception.ErrorCode;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class DeletePostCommand {
    UUID postId;
    UUID userId;

    public DeletePostCommand(UUID postId, UUID userId) {
        this.postId = postId;
        this.userId = userId;
        validate();
    }

    private void validate() {
        if (postId == null || userId == null) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
