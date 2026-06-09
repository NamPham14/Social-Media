package com.social_media.postservice.api.dto;

import com.social_media.postservice.domain.aggreate.Bookmark;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BookmarkResponse {

    private UUID id;
    private UUID userId;
    private UUID postId;
    private LocalDateTime createdAt;

    public static BookmarkResponse from(Bookmark bookmark) {
        BookmarkResponse res = new BookmarkResponse();
        res.id = bookmark.getId();
        res.userId = bookmark.getUserId();
        res.postId = bookmark.getPostId();
        res.createdAt = bookmark.getCreatedAt();
        return res;
    }
}
