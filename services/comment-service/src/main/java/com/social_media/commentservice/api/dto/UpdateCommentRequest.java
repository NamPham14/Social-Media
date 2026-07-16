package com.social_media.commentservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCommentRequest {
    @NotBlank
    @Size(max = 1000)
    private String content;
}
