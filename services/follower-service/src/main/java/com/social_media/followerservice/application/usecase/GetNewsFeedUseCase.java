package com.social_media.followerservice.application.usecase;
import com.social_media.followerservice.api.dto.FeedResponse;
import java.util.List;

public interface GetNewsFeedUseCase {
    List<FeedResponse> execute(Long currentUserId, int page, int size);
}
