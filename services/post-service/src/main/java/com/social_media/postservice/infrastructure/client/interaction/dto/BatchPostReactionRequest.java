package com.social_media.postservice.infrastructure.client.interaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

// Hiếu thêm
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchPostReactionRequest {
    private List<UUID> postIds;
}
