package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.api.dto.FeedResponse;
import java.util.List;
import java.util.UUID;

public interface GetNewsFeedUseCase {
    List<FeedResponse> execute(UUID currentUserId, int page, int size);
}
