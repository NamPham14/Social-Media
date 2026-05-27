package com.social_media.postservice.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeletePostRequest {
    @NotNull(message = "Post ID is required")
    private UUID postId;

    @NotNull(message = "User ID is required")
    private UUID userId;
}
