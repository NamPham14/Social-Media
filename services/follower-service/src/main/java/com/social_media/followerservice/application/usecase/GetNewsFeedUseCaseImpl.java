package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.api.dto.FeedResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GetNewsFeedUseCaseImpl implements GetNewsFeedUseCase {

    @Override
    public List<FeedResponse> getNewsFeed(Long userId) {

        return Collections.emptyList();
    }
}
