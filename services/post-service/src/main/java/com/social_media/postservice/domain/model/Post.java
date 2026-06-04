package com.social_media.postservice.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "posts",
        indexes = {
                @Index(name = "idx_posts_user_id", columnList = "user_id"),
                @Index(name = "idx_posts_created_at", columnList = "created_at")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE posts SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
public class Post {

    public enum Status {
        PUBLIC,
        PRIVATE
    }

    public enum ModerationStatus {
        DRAFT,
        PENDING,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(columnDefinition = "TEXT")
    private String caption;

    @Column(name = "location_name", length = 100)
    private String locationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false)
    @Builder.Default
    private ModerationStatus moderationStatus = ModerationStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private UUID approvedBy;
    private LocalDateTime approvedAt;

    @Column(name = "reject_reason", length = 500) // Cho phép null vì bài APPROVED hoặc DRAFT thì không có lý do
    private String rejectReason;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PostMedia> medias = new ArrayList<>();



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

    public static Post draft(UUID userId, String caption, String locationName) {

        Post post = new Post();

        post.userId = userId;
        post.caption = caption;
        post.locationName = locationName;

        post.status = Status.PRIVATE;
        post.moderationStatus = ModerationStatus.DRAFT;

        post.medias = new ArrayList<>();

        post.createdAt = LocalDateTime.now();
        post.updatedAt = LocalDateTime.now();

        post.deleted = false;
        post.deletedAt = null;

        return post;
    }


    public void update(String caption, String locationName) {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Deleted post cannot be updated"
            );
        }

        if (this.moderationStatus == ModerationStatus.PENDING) {
            throw new IllegalStateException(
                    "Pending post cannot be edited"
            );
        }

        if (caption != null && caption.length() > 2200) {
            throw new IllegalArgumentException(
                    "Caption exceeds maximum length"
            );
        }

        this.caption = caption;
        this.locationName = locationName;

        if (this.moderationStatus == ModerationStatus.APPROVED) {
            this.moderationStatus = ModerationStatus.PENDING;
        }
    }


    public void changeVisibility(Status newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Visibility status cannot be null");
        }

        if (this.deleted) {
            throw new IllegalStateException("Cannot change visibility of a deleted post");
        }

        if (newStatus == Status.PUBLIC && this.moderationStatus != ModerationStatus.APPROVED) {
            throw new IllegalStateException("Bài viết chưa được phê duyệt, không thể chuyển sang chế độ công khai.");
        }

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void addMedia(PostMedia media) {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Cannot add media to deleted post"
            );
        }

        if (this.moderationStatus == ModerationStatus.PENDING) {
            throw new IllegalStateException(
                    "Pending post cannot be edited"
            );
        }

        if (media == null) {
            throw new IllegalArgumentException(
                    "Media cannot be null"
            );
        }

        medias.add(media);
        media.setPost(this);

        if (this.moderationStatus == ModerationStatus.APPROVED) {
            this.moderationStatus = ModerationStatus.PENDING;
        }
    }

    public void removeMedia(PostMedia media) {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Cannot remove media from deleted post"
            );
        }

        if (this.moderationStatus == ModerationStatus.PENDING) {
            throw new IllegalStateException(
                    "Pending post cannot be edited"
            );
        }

        if (media == null) {
            throw new IllegalArgumentException(
                    "Media cannot be null"
            );
        }

        medias.remove(media);
        media.setPost(null);

        if (this.moderationStatus == ModerationStatus.APPROVED) {
            this.moderationStatus = ModerationStatus.PENDING;
        }
    }


    public void softDelete() {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Post already deleted"
            );
        }

        this.deleted = true;
        this.status = Status.PRIVATE;
        this.deletedAt = LocalDateTime.now();
    }


    public void submitForApproval() {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Deleted post cannot be submitted"
            );
        }

        if (this.moderationStatus != ModerationStatus.DRAFT
                && this.moderationStatus != ModerationStatus.REJECTED) {

            throw new IllegalStateException(
                    "Only DRAFT or REJECTED posts can be submitted"
            );
        }

        this.moderationStatus = ModerationStatus.PENDING;
    }

    public void approve(UUID adminId) {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Deleted post cannot be approved"
            );
        }

        if (this.moderationStatus != ModerationStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING posts can be approved"
            );
        }
        this.approvedBy = adminId; // Lưu vết ông Admin
        this.approvedAt = LocalDateTime.now();
        this.status = Status.PUBLIC;
        this.moderationStatus = ModerationStatus.APPROVED;
    }

    public void reject(UUID adminId, String reason) {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Deleted post cannot be rejected"
            );
        }

        if (this.moderationStatus != ModerationStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING posts can be rejected"
            );
        }


        this.moderationStatus = ModerationStatus.REJECTED;
        this.status = Status.PRIVATE;

        this.rejectReason = reason;
        this.updatedAt = LocalDateTime.now();

        this.approvedBy = adminId;
    }

    public void moveToDraft() {

        if (this.deleted) {
            throw new IllegalStateException(
                    "Deleted post cannot be moved to draft"
            );
        }

        if (this.moderationStatus == ModerationStatus.PENDING) {
            throw new IllegalStateException(
                    "Pending posts cannot be moved to draft"
            );
        }

        this.moderationStatus = ModerationStatus.DRAFT;
    }

    public boolean isApproved() {
        return this.moderationStatus == ModerationStatus.APPROVED;
    }

    public boolean isDraft() {
        return this.moderationStatus == ModerationStatus.DRAFT;
    }

    public boolean isPending() {
        return this.moderationStatus == ModerationStatus.PENDING;
    }

    public boolean isRejected() {
        return this.moderationStatus == ModerationStatus.REJECTED;
    }

}