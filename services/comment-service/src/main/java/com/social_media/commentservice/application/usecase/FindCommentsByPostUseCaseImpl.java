package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.dto.PageResponse;
import com.social_media.commentservice.application.mapper.CommentMapper;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindCommentsByPostUseCaseImpl implements FindCommentsByPostUseCase {

    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> execute(UUID postId, int page, int size) {
        var result = commentRepository.findVisibleByPostId(postId, page, size);
        return new PageResponse<>(result.content().stream()
                .map(comment -> CommentMapper.toResponse(comment, comment.isDeleted())).toList(),
                result.page(), result.size(), result.totalElements(), result.totalPages());
    }
}
