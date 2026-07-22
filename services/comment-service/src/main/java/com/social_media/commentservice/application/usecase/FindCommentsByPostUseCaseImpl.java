package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.api.dto.PageResponse;
import com.social_media.commentservice.application.mapper.CommentMapper;
import com.social_media.commentservice.domain.repository.CommentRepository;
import com.social_media.commentservice.application.service.CommentInteractionEnricher;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindCommentsByPostUseCaseImpl implements FindCommentsByPostUseCase {

    private final CommentRepository commentRepository;
    private final CommentInteractionEnricher interactionEnricher;
    private final PostAvailabilityPort postAvailabilityPort;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CommentResponse> execute(UUID postId, int page, int size, UUID actorId) {
        postAvailabilityPort.getCommentable(postId, actorId);
        var result = commentRepository.findVisibleByPostId(postId, page, size);
        var replyCounts = commentRepository.countActiveReplies(
                result.content().stream().map(com.social_media.commentservice.domain.model.Comment::getId).toList());
        var responses = result.content().stream()
                .map(comment -> CommentMapper.toResponse(comment, comment.isDeleted(),
                        replyCounts.getOrDefault(comment.getId(), 0L))).toList();
        return new PageResponse<>(interactionEnricher.enrich(responses, actorId), result.page(), result.size(),
                result.totalElements(), result.totalPages());
    }
}
