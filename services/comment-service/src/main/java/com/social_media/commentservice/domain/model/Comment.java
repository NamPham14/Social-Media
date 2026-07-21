package com.social_media.commentservice.domain.model;

import com.social_media.commentservice.domain.exception.CommentAccessDeniedException;
import com.social_media.commentservice.domain.exception.CommentAlreadyDeletedException;
import com.social_media.commentservice.domain.exception.InvalidCommentException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_post_id", columnList = "post_id"),
                @Index(name = "idx_comments_parent_id", columnList = "parent_id")
        }
)
@Getter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "post_id", nullable = false, updatable = false)
    private UUID postId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "author_name", length = 100)
    private String authorName;

    @Column(name = "author_avatar_url", length = 500)
    private String authorAvatarUrl;


    @Column(name = "parent_id")
    private UUID parentId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Comment create(UUID postId, UUID userId,String authorName, String authorAvatarUrl, UUID parentId, String content) {
        if (postId == null) {
            throw new InvalidCommentException("Post id is required");
        }
        if (userId == null) {
            throw new InvalidCommentException("Actor id is required");
        }
        if (content == null || content.isBlank()) {
            throw new InvalidCommentException("Comment content is required");
        }
        if (content.length() > 1000) {
            throw new InvalidCommentException("Comment content exceeds maximum length");
        }

        Comment comment = new Comment();
        comment.postId = postId;
        comment.userId = userId;
        comment.authorName = authorName;
        comment.authorAvatarUrl = authorAvatarUrl;
        comment.parentId = parentId;
        comment.content = content.trim();
        comment.deleted = false;
        return comment;
    }

    public void updateContent(UUID actorId, String newContent) {
        ensureOwner(actorId);
        if (deleted) {
            throw new CommentAlreadyDeletedException();
        }
        if (newContent == null || newContent.isBlank()) {
            throw new InvalidCommentException("Comment content is required");
        }
        if (newContent.length() > 1000) {
            throw new InvalidCommentException("Comment content exceeds maximum length");
        }
        this.content = newContent.trim();
    }

    public boolean softDelete(UUID actorId) {
        ensureOwner(actorId);
        if (deleted) {
            return false;
        }
        this.deleted = true;
        return true;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private void ensureOwner(UUID actorId) {
        if (!userId.equals(actorId)) {
            throw new CommentAccessDeniedException();
        }
    }
}
