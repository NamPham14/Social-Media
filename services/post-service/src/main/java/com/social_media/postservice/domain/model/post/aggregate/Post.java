package com.social_media.postservice.domain.model.post.aggregate;

import com.social_media.postservice.domain.model.post.valueobject.ModerationStatus;
import com.social_media.postservice.domain.model.post.valueobject.PostStatus;
import com.social_media.postservice.domain.model.post.entity.PostMedia;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Getter
public class Post {

    private UUID id;

    private UUID userId;

    private String caption;

    private String locationName;

    private PostStatus status;

    @Builder.Default
    private ModerationStatus moderationStatus = ModerationStatus.NONE;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    private UUID removedBy;
    private LocalDateTime removedAt;

    @Builder.Default
    private List<PostMedia> medias = new ArrayList<>();

    public static Post create(UUID userId, String caption, String locationName) {
        Post post = new Post();
        post.userId = userId;
        post.caption = caption;
        post.locationName = locationName;
        post.status = PostStatus.PUBLIC;
        post.moderationStatus = ModerationStatus.NONE;
        post.medias = new ArrayList<>();
        post.createdAt = LocalDateTime.now();
        post.updatedAt = LocalDateTime.now();
        post.deleted = false;
        post.deletedAt = null;
        return post;
    }

    public void update(String caption, String locationName) {
        if (this.deleted) {
            throw new IllegalStateException("Deleted post cannot be updated");
        }
        if (caption != null && caption.length() > 2200) {
            throw new IllegalArgumentException("Caption exceeds maximum length");
        }
        this.caption = caption;
        this.locationName = locationName;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeVisibility(PostStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Visibility status cannot be null");
        }
        if (this.deleted) {
            throw new IllegalStateException("Cannot change visibility of a deleted post");
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void addMedia(PostMedia media) {
        if (this.deleted) {
            throw new IllegalStateException("Cannot add media to deleted post");
        }
        if (media == null) {
            throw new IllegalArgumentException("Media cannot be null");
        }
        medias.add(media);
    }

    public void removeMedia(PostMedia media) {
        if (this.deleted) {
            throw new IllegalStateException("Cannot remove media from deleted post");
        }
        if (media == null) {
            throw new IllegalArgumentException("Media cannot be null");
        }
        medias.remove(media);
    }

    public void softDelete() {
        if (this.deleted) {
            throw new IllegalStateException("Post already deleted");
        }
        this.deleted = true;
        this.moderationStatus = ModerationStatus.REMOVED;
        this.deletedAt = LocalDateTime.now();
    }

    public void removeByAdmin(UUID adminId) {
        if (this.deleted) {
            throw new IllegalStateException("Post already deleted");
        }
        this.deleted = true;
        this.moderationStatus = ModerationStatus.REMOVED;
        this.status = PostStatus.PRIVATE;
        this.removedBy = adminId;
        this.removedAt = LocalDateTime.now();
        this.deletedAt = LocalDateTime.now();
    }
}
