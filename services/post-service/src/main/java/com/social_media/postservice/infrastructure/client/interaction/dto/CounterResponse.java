package com.social_media.postservice.infrastructure.client.interaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounterResponse {
    private String targetType;
    private UUID targetId;
    private int likeCount;
    private int clapCount;
}
