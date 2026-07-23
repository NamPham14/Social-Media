package com.social_media.postservice.domain.model.post.service;

import com.social_media.postservice.domain.model.post.valueobject.ModerationResult;

public interface ModerationPort {
    ModerationResult check(String content);
}
