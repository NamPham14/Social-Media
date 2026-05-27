package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.domain.UserProfile;

public interface CreateProfileUseCase {
    UserProfile execute(CreateProfileCommand command);
}
