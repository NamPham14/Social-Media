package com.social_media.followerservice.domain.model.follow.aggregate;

import com.social_media.followerservice.domain.shared.valueobject.UserId;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowRelation {
    private UUID id;
    private UserId followerId;
    private UserId followingId;
    private LocalDateTime createdAt;

    public static FollowRelation create(UserId followerId, UserId followingId) {
        return FollowRelation.builder()
                .followerId(followerId)
                .followingId(followingId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
