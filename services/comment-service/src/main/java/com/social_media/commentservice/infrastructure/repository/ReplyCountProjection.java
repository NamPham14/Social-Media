package com.social_media.commentservice.infrastructure.repository;

import java.util.UUID;

public interface ReplyCountProjection {
    UUID getParentId();
    long getReplyCount();
}
