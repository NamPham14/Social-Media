package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;

public interface CreateProfileUseCase {
    Profile execute(CreateProfileCommand command);
}

