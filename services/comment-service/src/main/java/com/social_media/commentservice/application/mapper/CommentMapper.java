package com.social_media.commentservice.application.mapper;

import com.social_media.commentservice.api.dto.CommentResponse;
import com.social_media.commentservice.domain.model.Comment;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentResponse toResponse(Comment comment) {
        return toResponse(comment, false);
    }

    public static CommentResponse toResponse(Comment comment, boolean hideDeletedContent) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .authorName(comment.getAuthorName())
                .authorAvatarUrl(comment.getAuthorAvatarUrl())
                .parentId(comment.getParentId())
                .content(hideDeletedContent ? "[deleted]" : comment.getContent())
                .deleted(comment.isDeleted())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
