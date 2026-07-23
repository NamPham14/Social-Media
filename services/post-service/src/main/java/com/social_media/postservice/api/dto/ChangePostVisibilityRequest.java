package com.social_media.postservice.api.dto;

import com.social_media.postservice.domain.model.post.valueobject.PostStatus;
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
public class ChangePostVisibilityRequest {
    @NotNull(message = "Post ID is required")
    private UUID postId;

    @NotNull(message = "New status is required")
    private PostStatus newStatus;
}
