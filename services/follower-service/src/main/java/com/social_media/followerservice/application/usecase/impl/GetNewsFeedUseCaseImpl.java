package com.social_media.followerservice.application.usecase.impl;

import com.social_media.followerservice.api.dto.FeedResponse;
import com.social_media.followerservice.application.usecase.GetNewsFeedUseCase;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class GetNewsFeedUseCaseImpl implements GetNewsFeedUseCase {
    @Override
    public List<FeedResponse> execute(UUID currentUserId, int page, int size) {
        return Collections.emptyList();
    }
}
