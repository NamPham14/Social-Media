package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.mapper.CommentMapper;
import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCommentUseCaseImpl implements CreateCommentUseCase {

    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentResponse execute(CreateCommentCommand command) {
        Comment comment = Comment.create(command.postId(), command.userId(), command.parentId(), command.content());
        return CommentMapper.toResponse(commentRepository.save(comment));
    }
}
