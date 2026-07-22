package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.dto.PageResponse;

import java.util.UUID;

public interface FindRepliesUseCase {
    PageResponse<CommentResponse> execute(UUID parentId, int page, int size, UUID actorId);
}
