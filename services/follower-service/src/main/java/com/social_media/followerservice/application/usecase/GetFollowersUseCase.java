package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.api.dto.FollowResponse;
import org.springframework.data.domain.Page;

public interface GetFollowersUseCase {
    Page<FollowResponse> execute(Long userId, int page, int size);
}
