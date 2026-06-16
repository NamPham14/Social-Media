package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.application.command.UpdateProfileCommand;
import com.social_media.profileservice.domain.model.aggregate.Profile;

public interface UpdateProfileUseCase {
    Profile execute(UpdateProfileCommand command);
}
