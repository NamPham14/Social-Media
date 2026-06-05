package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.command.CreateCommentCommand;

public interface CreateCommentUseCase {
    CommentResponse execute(CreateCommentCommand command);
}
