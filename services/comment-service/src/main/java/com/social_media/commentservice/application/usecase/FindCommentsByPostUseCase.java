package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.dto.PageResponse;

import java.util.UUID;

public interface FindCommentsByPostUseCase {
    PageResponse<CommentResponse> execute(UUID postId, int page, int size, UUID actorId);
}
