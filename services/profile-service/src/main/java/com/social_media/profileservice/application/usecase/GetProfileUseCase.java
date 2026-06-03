package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.domain.UserProfile;

import java.util.UUID;

public interface GetProfileUseCase {
    UserProfile execute(UUID id);
}
