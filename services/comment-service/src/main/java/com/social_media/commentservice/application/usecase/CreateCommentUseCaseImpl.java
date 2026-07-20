package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.mapper.CommentMapper;
import com.social_media.commentservice.application.event.CommentNotificationEvent;
import com.social_media.commentservice.application.port.out.CommentEventOutbox;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.exception.CommentNotFoundException;
import com.social_media.commentservice.domain.exception.InvalidCommentException;
import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCommentUseCaseImpl implements CreateCommentUseCase {

    private final CommentRepository commentRepository;
    private final PostAvailabilityPort postAvailabilityPort;
    private final CommentEventOutbox eventOutbox;

    @Override
    @Transactional
    public CommentResponse execute(CreateCommentCommand command) {
        PostAvailabilityPort.AvailablePost post =
                postAvailabilityPort.getCommentable(command.postId(), command.actorId());
        UUID recipientId = post.ownerId();
        if (command.parentId() != null) {
            Comment parent = commentRepository.findById(command.parentId())
                    .orElseThrow(() -> new CommentNotFoundException(command.parentId()));
            if (!parent.getPostId().equals(command.postId())) {
                throw new InvalidCommentException("Parent comment belongs to another post");
            }
            if (parent.getParentId() != null) {
                throw new InvalidCommentException("Only one reply level is supported");
            }
            if (parent.isDeleted()) {
                throw new InvalidCommentException("Cannot reply to a deleted comment");
            }
            recipientId = parent.getUserId();
        }
        Comment comment = Comment.create(command.postId(), command.actorId(), command.parentId(), command.content());
        Comment saved = commentRepository.save(comment);
        if (!saved.getUserId().equals(recipientId)) {
            eventOutbox.append(CommentNotificationEvent.from(saved, recipientId));
        }
        return CommentMapper.toResponse(saved);
    }
}
