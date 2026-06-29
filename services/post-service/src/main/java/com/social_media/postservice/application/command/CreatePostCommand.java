package com.social_media.postservice.application.command;

import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class CreatePostCommand {
    UUID userId;
    String caption;
    String locationName;
    List<MultipartFile> images;

    public CreatePostCommand(UUID userId, String caption, String locationName, List<MultipartFile> images) {
        this.userId = userId;
        this.caption = caption;
        this.locationName = locationName;
        this.images = images;
        validate();
    }

    private void validate() {
        if (userId == null) {
            throw new com.social_media.postservice.application.exception.ResourceNotFoundException(); // Or a specific INVALID_INPUT error if available
        }
        boolean hasCaption = caption != null && !caption.isBlank();
        boolean hasImages = images != null && !images.isEmpty();
        if (!hasCaption && !hasImages) {
            throw new com.social_media.postservice.application.exception.ResourceNotFoundException();
        }
    }
}
