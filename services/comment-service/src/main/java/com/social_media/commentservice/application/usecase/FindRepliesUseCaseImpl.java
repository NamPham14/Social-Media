package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.dto.PageResponse;
import com.social_media.commentservice.application.mapper.CommentMapper;
import com.social_media.commentservice.domain.exception.CommentNotFoundException;
import com.social_media.commentservice.domain.repository.CommentRepository;
import com.social_media.commentservice.application.service.CommentInteractionEnricher;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindRepliesUseCaseImpl implements FindRepliesUseCase {
    private final CommentRepository commentRepository;
    private final CommentInteractionEnricher interactionEnricher;
    private final PostAvailabilityPort postAvailabilityPort;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> execute(UUID parentId, int page, int size, UUID actorId) {
        var parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentNotFoundException(parentId));
        if (parent.getParentId() != null) throw new CommentNotFoundException(parentId);
        postAvailabilityPort.getCommentable(parent.getPostId(), actorId);
        var result = commentRepository.findActiveReplies(parentId, page, size);
        var responses = result.content().stream().map(CommentMapper::toResponse).toList();
        return new PageResponse<>(interactionEnricher.enrich(responses, actorId),
                result.page(), result.size(), result.totalElements(), result.totalPages());
    }
}
