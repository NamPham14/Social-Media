package com.social_media.commentservice.infrastructure.repository;

import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public Optional<Comment> findById(UUID id) {
        return commentJpaRepository.findById(id);
    }

    @Override
    public List<Comment> findActiveByPostId(UUID postId) {
        return commentJpaRepository.findByPostIdAndDeletedFalseOrderByCreatedAtAsc(postId);
    }

    @Override
    public Comment save(Comment comment) {
        return commentJpaRepository.save(comment);
    }
}
