package com.social_media.postservice.application.command;

import com.social_media.postservice.application.dto.MediaResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DraftPostCommand {

    private UUID id;
    private UUID userId;

    private String caption;
    private String locationName;

    private String status;
    private String moderationStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<MultipartFile> images;
}
