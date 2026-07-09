package com.social_media.postservice.domain.model.bookmark.aggregate;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Getter
public class Bookmark {

    private UUID id;

    private UUID userId;

    private UUID postId;

    private LocalDateTime createdAt;

    public static Bookmark create(UUID userId, UUID postId) {
        Bookmark bookmark = new Bookmark();
        bookmark.userId = userId;
        bookmark.postId = postId;
        bookmark.createdAt = LocalDateTime.now();
        return bookmark;
    }
}
