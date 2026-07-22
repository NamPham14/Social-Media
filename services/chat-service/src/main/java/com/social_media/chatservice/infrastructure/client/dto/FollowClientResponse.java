package com.social_media.chatservice.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowClientResponse {
    private UUID id;
    private UUID followerId;
    private UUID followingId;
}
