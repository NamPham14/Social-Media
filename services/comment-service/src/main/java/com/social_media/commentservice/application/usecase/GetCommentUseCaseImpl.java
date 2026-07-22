package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
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
public class GetCommentUseCaseImpl implements GetCommentUseCase {
    private final CommentRepository commentRepository;
    private final CommentInteractionEnricher interactionEnricher;
    private final PostAvailabilityPort postAvailabilityPort;

    @Override
    @Transactional(readOnly = true)
    public CommentResponse execute(UUID commentId, UUID actorId) {
        CommentResponse response = commentRepository.findById(commentId)
                .map(comment -> CommentMapper.toResponse(comment, comment.isDeleted(),
                        commentRepository.countActiveReplies(comment.getId())))
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        postAvailabilityPort.getCommentable(response.getPostId(), actorId);
        return interactionEnricher.enrich(response, actorId);
    }
}
