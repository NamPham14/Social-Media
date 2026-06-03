package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.application.command.UpdateProfileCommand;
import com.social_media.profileservice.domain.UserProfile;

public interface UpdateProfileUseCase {
    UserProfile execute(UpdateProfileCommand command);
}
