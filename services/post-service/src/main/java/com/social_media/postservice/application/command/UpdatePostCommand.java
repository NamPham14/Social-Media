package com.social_media.postservice.application.command;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostCommand {
    private UUID id;
    private UUID userId;

    private String caption;
    private String locationName;


    private List<String> remainImageUrls;
    private List<MultipartFile> newImages;

    private String status;
    private String moderationStatus;

    private LocalDateTime updatedAt;
}
