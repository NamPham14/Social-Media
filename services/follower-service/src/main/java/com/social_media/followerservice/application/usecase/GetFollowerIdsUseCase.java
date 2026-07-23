package com.social_media.followerservice.application.usecase;

import org.springframework.data.domain.Page;

import java.util.UUID;

public interface GetFollowerIdsUseCase {
    Page<UUID> execute(UUID userId, int page, int size);
}
