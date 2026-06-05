package com.social_media.commentservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCommentRequest {

    @NotNull
    private UUID postId;

    @NotNull
    private UUID userId;

    private UUID parentId;

    @NotBlank
    @Size(max = 1000)
    private String content;
}
