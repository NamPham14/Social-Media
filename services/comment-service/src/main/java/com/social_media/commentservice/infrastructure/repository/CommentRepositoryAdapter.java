package com.social_media.commentservice.infrastructure.repository;

import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import com.social_media.commentservice.domain.model.PageResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Optional<Comment> findById(UUID id) {
        return commentJpaRepository.findById(id);
    }

    @Override
    public PageResult<Comment> findVisibleByPostId(UUID postId, int page, int size) {
        var result = commentJpaRepository.findVisibleByPostId(postId,
                PageRequest.of(page, size, Sort.by("createdAt").ascending()));
        return new PageResult<>(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public PageResult<Comment> findActiveReplies(UUID parentId, int page, int size) {
        var result = commentJpaRepository.findByParentIdAndDeletedFalse(parentId,
                PageRequest.of(page, size, Sort.by("createdAt").ascending()));
        return new PageResult<>(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public List<Comment> findActiveRepliesList(UUID parentId) {
        return commentJpaRepository.findByParentIdAndDeletedFalse(parentId);
    }

    @Override
    public boolean hasActiveReplies(UUID commentId) {
        return commentJpaRepository.existsByParentIdAndDeletedFalse(commentId);
    }

    @Override
    public long countActiveReplies(UUID parentId) {
        return commentJpaRepository.countByParentIdAndDeletedFalse(parentId);
    }

    @Override
    public Map<UUID, Long> countActiveReplies(Collection<UUID> parentIds) {
        if (parentIds.isEmpty()) return Map.of();
        Map<UUID, Long> counts = new HashMap<>();
        commentJpaRepository.countActiveRepliesByParentIds(parentIds)
                .forEach(row -> counts.put(row.getParentId(), row.getReplyCount()));
        return counts;
    }

    @Override
    public long countActiveByPostId(UUID postId) {
        return commentJpaRepository.countByPostIdAndDeletedFalse(postId);
    }

    @Override
    public Map<UUID, Long> countActiveByPostIds(Collection<UUID> postIds) {
        if (postIds.isEmpty()) {
            return Map.of();
        }
        Map<UUID, Long> counts = new HashMap<>();
        commentJpaRepository.countActiveByPostIds(postIds)
                .forEach(result -> counts.put(result.getPostId(), result.getCommentCount()));
        return counts;
    }

    @Override
    public List<UUID> findActiveIdsByPostId(UUID postId) {
        return commentJpaRepository.findActiveIdsByPostId(postId);
    }

    @Override
    public int softDeleteAllByPostId(UUID postId) {
        return commentJpaRepository.softDeleteAllByPostId(postId);
    }

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }
}
