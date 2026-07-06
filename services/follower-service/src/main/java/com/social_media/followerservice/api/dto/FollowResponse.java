package com.social_media.followerservice.api.dto;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FollowResponse {
    private Long id;
    private Long followerId;
    private Long followingId;
    private LocalDateTime followedAt;
}
