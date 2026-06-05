package com.social_media.commentservice.application.usecase;

import java.util.UUID;

public interface DeleteCommentUseCase {
    void execute(UUID commentId, UUID userId);
}
