package com.social_media.postservice.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreatePostCommand {

    private UUID userId;

    private String caption;
    private String locationName;

    private List<MultipartFile> images;
}
