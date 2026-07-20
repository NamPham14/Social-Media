package com.social_media.commentservice.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record BatchCommentCountRequest(
        @NotEmpty @Size(max = 100) List<@NotNull UUID> postIds) {
}
