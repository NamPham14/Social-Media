package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentCountResponse;

import java.util.List;
import java.util.UUID;

public interface GetCommentCountsUseCase {
    CommentCountResponse get(UUID postId);

    List<CommentCountResponse> getBatch(List<UUID> postIds);
}
