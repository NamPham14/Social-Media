package com.social_media.postservice.application.command;

import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Value
@Builder
public class UpdatePostCommand {
    UUID id;
    UUID userId;
    String caption;
    String locationName;
    List<String> remainImageUrls;
    List<MultipartFile> newImages;

    public UpdatePostCommand(UUID id, UUID userId, String caption, String locationName, List<String> remainImageUrls, List<MultipartFile> newImages) {
        this.id = id;
        this.userId = userId;
        this.caption = caption;
        this.locationName = locationName;
        this.remainImageUrls = remainImageUrls;
        this.newImages = newImages;
        validate();
    }

    private void validate() {
        if (id == null || userId == null) {
            throw new com.social_media.postservice.application.exception.ResourceNotFoundException();
        }
    }
}
