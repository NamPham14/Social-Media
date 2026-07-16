package com.social_media.commentservice.infrastructure.repository;

import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    public boolean hasActiveReplies(UUID commentId) {
        return commentJpaRepository.existsByParentIdAndDeletedFalse(commentId);
    }

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }
}
