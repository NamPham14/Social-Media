package com.social_media.followerservice.application.usecase;

import com.social_media.followerservice.api.dto.FollowResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GetFollowingUseCase {
    Page<FollowResponse> execute(UUID userId, int page, int size);
}
