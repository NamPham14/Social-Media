package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.exception.CommentNotFoundException;
import com.social_media.commentservice.domain.repository.CommentRepository;
import com.social_media.commentservice.application.event.CommentDeletedEvent;
import com.social_media.commentservice.application.port.out.CommentEventOutbox;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCommentUseCaseImpl implements DeleteCommentUseCase {

    private final CommentRepository commentRepository;
    private final PostAvailabilityPort postAvailabilityPort;
    private final CommentEventOutbox eventOutbox;

    @Override
    @Transactional
    public void execute(UUID commentId, UUID actorId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        UUID postOwnerId = comment.getUserId().equals(actorId) ? null
                : postAvailabilityPort.getCommentable(comment.getPostId(), actorId).ownerId();
        if (comment.softDelete(actorId, postOwnerId)) {
            commentRepository.save(comment);
            eventOutbox.append(CommentDeletedEvent.create(comment.getId(), comment.getPostId(), actorId));

            if (comment.getParentId() == null) {
                java.util.List<Comment> replies = commentRepository.findActiveRepliesList(commentId);
                for (Comment reply : replies) {
                    if (reply.softDelete(actorId, postOwnerId)) {
                        commentRepository.save(reply);
                        eventOutbox.append(CommentDeletedEvent.create(reply.getId(), reply.getPostId(), actorId));
                    }
                }
            }
        }
    }
}
