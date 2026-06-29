package com.social_media.followerservice.domain.model.follower.aggregate;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Follower {

    private UUID id;

    private UUID followerId;

    private UUID followedUserId;

    private String status;

    private LocalDateTime followedAt;

    private LocalDateTime unfollowedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    public void validate() {
        if (followerId != null && followerId.equals(followedUserId)) {
            throw new IllegalArgumentException("A user cannot follow themselves");
        }
    }
}
