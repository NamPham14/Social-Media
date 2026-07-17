package com.social_media.commentservice.infrastructure.repository;

import java.util.UUID;

public interface CommentCountProjection {
    UUID getPostId();

    long getCommentCount();
}
