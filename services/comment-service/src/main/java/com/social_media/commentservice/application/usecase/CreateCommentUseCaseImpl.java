package com.social_media.commentservice.application.usecase;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.application.command.CreateCommentCommand;
import com.social_media.commentservice.application.mapper.CommentMapper;
import com.social_media.commentservice.application.port.out.PostAvailabilityPort;
import com.social_media.commentservice.domain.exception.CommentNotFoundException;
import com.social_media.commentservice.domain.exception.InvalidCommentException;
import com.social_media.commentservice.domain.model.Comment;
import com.social_media.commentservice.domain.repository.CommentRepository;
import com.social_media.commentservice.infrastructure.client.profile.ProfileClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CreateCommentUseCaseImpl implements CreateCommentUseCase {

    private final CommentRepository commentRepository;
    private final PostAvailabilityPort postAvailabilityPort;
    private final ProfileClient profileClient;

    @Override
    @Transactional
    public CommentResponse execute(CreateCommentCommand command) {
        postAvailabilityPort.ensureCommentable(command.postId(), command.actorId());
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
        }
        // Xin Tên và Ảnh từ Profile
        String authorName = "Unknown";
        String authorAvatar = null;
        try {
            Map<String, Object> profileData = profileClient.getProfileById(command.actorId());
            if (profileData != null && profileData.get("data") != null) {
                Map<String, Object> data = (Map<String, Object>) profileData.get("data");
                authorName = (String) data.get("username");
                authorAvatar = (String) data.get("avatarUrl");
            }
        } catch (Exception e) {
            System.out.println("Lỗi gọi Profile: " + e.getMessage());
        }
        Comment comment = Comment.create(command.postId(), command.actorId(),  authorName, authorAvatar,command.parentId(), command.content());
        return CommentMapper.toResponse(commentRepository.save(comment));
    }
}
