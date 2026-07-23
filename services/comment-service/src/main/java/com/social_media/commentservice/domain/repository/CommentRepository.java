package com.social_media.commentservice.domain.repository;

import com.social_media.commentservice.domain.model.Comment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import com.social_media.commentservice.domain.model.PageResult;

public interface CommentRepository {
    Optional<Comment> findById(UUID id);

    PageResult<Comment> findVisibleByPostId(UUID postId, int page, int size);

    PageResult<Comment> findActiveReplies(UUID parentId, int page, int size);

    List<Comment> findActiveRepliesList(UUID parentId);

    boolean hasActiveReplies(UUID commentId);

    long countActiveReplies(UUID parentId);

    Map<UUID, Long> countActiveReplies(Collection<UUID> parentIds);

    long countActiveByPostId(UUID postId);

    Map<UUID, Long> countActiveByPostIds(Collection<UUID> postIds);

    List<UUID> findActiveIdsByPostId(UUID postId);

    int softDeleteAllByPostId(UUID postId);

    Comment save(Comment comment);
}
