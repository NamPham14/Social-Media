package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.mapper.CommentMapper;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindCommentsByPostUseCaseImpl implements FindCommentsByPostUseCase {

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> execute(UUID postId) {
        return commentRepository.findActiveByPostId(postId).stream()
                .map(CommentMapper::toResponse)
                .toList();
    }
}
