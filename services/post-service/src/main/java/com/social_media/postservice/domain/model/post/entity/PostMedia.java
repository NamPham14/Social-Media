package com.social_media.postservice.domain.model.post.entity;

//import com.social_media.postservice.domain.model.post.valueobject.MediaType;
import com.social_media.postservice.domain.model.post.valueobject.MediaType;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Builder
public class PostMedia {

    private UUID id;

    private String mediaUrl;

    private String publicId;

    private MediaType mediaType;

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
}

