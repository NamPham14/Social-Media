package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import java.util.UUID;

public interface GetCommentUseCase {
    CommentResponse execute(UUID commentId);
}
