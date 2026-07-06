package com.social_media.followerservice.api.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FollowRequest {
    private Long followerId;
    private Long followingId;
}
