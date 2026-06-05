package com.social_media.commentservice.domain.model;

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

    public static Comment create(UUID postId, UUID userId, UUID parentId, String content) {
        if (postId == null) {
            throw new IllegalArgumentException("Post id is required");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("Comment content exceeds maximum length");
        }

        Comment comment = new Comment();
        comment.postId = postId;
        comment.userId = userId;
        comment.parentId = parentId;
        comment.content = content.trim();
        comment.deleted = false;
        return comment;
    }

    public void updateContent(UUID actorId, String newContent) {
        ensureOwner(actorId);
        if (deleted) {
            throw new IllegalStateException("Deleted comment cannot be updated");
        }
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("Comment content is required");
        }
        if (newContent.length() > 1000) {
            throw new IllegalArgumentException("Comment content exceeds maximum length");
        }
        this.content = newContent.trim();
    }

    public void softDelete(UUID actorId) {
        ensureOwner(actorId);
        if (deleted) {
            throw new IllegalStateException("Comment already deleted");
        }
        this.deleted = true;
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
            throw new IllegalStateException("Only the comment owner can perform this action");
        }
    }
}
