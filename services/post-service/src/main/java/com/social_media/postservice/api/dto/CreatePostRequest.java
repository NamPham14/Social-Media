package com.social_media.postservice.api.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreatePostRequest {
    @Size(max = 2200, message = "Caption cannot exceed 2200 characters")
    private String caption;

    @Size(max = 100, message = "Location name cannot exceed 100 characters")
    private String locationName;


    private List<MultipartFile> images;


    public boolean isValid() {
        boolean hasCaption = caption != null && !caption.isBlank();
        boolean hasImages = images != null && !images.isEmpty();
        return hasCaption || hasImages;
    }
}
