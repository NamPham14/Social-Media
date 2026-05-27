package com.social_media.postservice.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "post_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMedia {

    public enum MediaType {
        IMAGE,
        VIDEO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "media_url", nullable = false, length = 500)
    private String mediaUrl;

    @Column(name = "public_id", nullable = false, length = 255)
    private String publicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    @Column(name = "order_index")
    @Builder.Default
    private Integer orderIndex = 0;

    public static PostMedia create(String publicId, String mediaUrl, MediaType mediaType, Integer orderIndex) {
        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException("Public ID cannot be blank");
        }
        if (mediaUrl == null || mediaUrl.isBlank()) {
            throw new IllegalArgumentException("Media URL cannot be blank");
        }

        PostMedia media = new PostMedia();
        media.publicId = publicId;
        media.mediaUrl = mediaUrl;
        media.mediaType = mediaType;
        media.orderIndex = (orderIndex != null) ? orderIndex : 0;

        return media;
    }


    public boolean isImage() {
        return this.mediaType == MediaType.IMAGE;
    }

    public boolean isVideo() {
        return this.mediaType == MediaType.VIDEO;
    }

    public void changeMediaUrl(String mediaUrl) {
        if (mediaUrl == null || mediaUrl.isBlank()) {
            throw new IllegalArgumentException("Media url cannot be blank");
        }

        this.mediaUrl = mediaUrl;
    }

    public void changeMedia(String publicId, String mediaUrl) {
        if (publicId == null || publicId.isBlank() || mediaUrl == null || mediaUrl.isBlank()) {
            throw new IllegalArgumentException("Public ID and Media URL cannot be blank");
        }
        this.publicId = publicId;
        this.mediaUrl = mediaUrl;
    }

    public void changeOrder(Integer orderIndex) {
        if (orderIndex == null || orderIndex < 0) {
            throw new IllegalArgumentException("Order index must be >= 0");
        }

        this.orderIndex = orderIndex;
    }

    public void attachToPost(Post post) {
        this.post = post;
    }

    public void detachPost() {
        this.post = null;
    }
}