package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;

import java.util.List;
import java.util.UUID;

public interface FindCommentsByPostUseCase {
    List<CommentResponse> execute(UUID postId);
}
