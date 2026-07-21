package com.social_media.postservice.application.service;

import com.social_media.postservice.domain.model.post.service.ModerationPort;
import com.social_media.postservice.domain.model.post.valueobject.ModerationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostModerationService {

    private final ModerationPort moderationPort;
    private final PostModerationApplier postModerationApplier;

    public void moderate(UUID postId, String caption) {
        ModerationResult result = moderationPort.check(caption);
        postModerationApplier.apply(postId, result);
    }
}