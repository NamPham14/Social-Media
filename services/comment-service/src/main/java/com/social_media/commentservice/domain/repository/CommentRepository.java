package com.social_media.commentservice.domain.repository;

import com.social_media.commentservice.domain.model.Comment;

import java.util.Optional;
import java.util.UUID;
import com.social_media.commentservice.domain.model.PageResult;

public interface CommentRepository {
    Optional<Comment> findById(UUID id);

    PageResult<Comment> findVisibleByPostId(UUID postId, int page, int size);

    boolean hasActiveReplies(UUID commentId);

    Comment save(Comment comment);
}
