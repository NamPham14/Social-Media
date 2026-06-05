package com.social_media.commentservice.domain.repository;

import com.social_media.commentservice.domain.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {
    Optional<Comment> findById(UUID id);

    List<Comment> findActiveByPostId(UUID postId);

    Comment save(Comment comment);
}
