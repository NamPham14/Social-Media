package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.domain.model.profile.aggregate.Profile;

import java.util.UUID;

public interface GetProfileUseCase {
    Profile execute(UUID id);
}
