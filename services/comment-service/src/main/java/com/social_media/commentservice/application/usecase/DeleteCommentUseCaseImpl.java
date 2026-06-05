package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCommentUseCaseImpl implements DeleteCommentUseCase {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void execute(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.softDelete(userId);
        commentRepository.save(comment);
    }
}
