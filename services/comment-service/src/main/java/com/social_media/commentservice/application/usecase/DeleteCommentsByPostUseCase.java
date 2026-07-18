package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.application.event.PostDeletedEvent;

public interface DeleteCommentsByPostUseCase {
    int execute(PostDeletedEvent event);
}
