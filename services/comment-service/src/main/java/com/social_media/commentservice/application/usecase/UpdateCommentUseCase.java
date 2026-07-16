package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import java.util.UUID;

public interface UpdateCommentUseCase {
    CommentResponse execute(UUID commentId, UUID actorId, String content);
}
