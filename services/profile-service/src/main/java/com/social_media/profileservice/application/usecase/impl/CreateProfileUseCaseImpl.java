package com.social_media.profileservice.application.usecase.impl;

import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.application.exception.DuplicateProfileException;
import com.social_media.profileservice.application.usecase.CreateProfileUseCase;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import com.social_media.profileservice.domain.shared.valueobject.UserProfileId;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProfileUseCaseImpl implements CreateProfileUseCase {

    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public Profile execute(CreateProfileCommand command) {

        if (profileRepository.findById(command.id()).isPresent()) {
            throw new DuplicateProfileException();
        }

        //  Chuyển đổi UUID sang Value Object UserProfileId
        UserProfileId userProfileId = UserProfileId.from(command.id());

        Profile profile = Profile.create(
                userProfileId,
                command.username(),
                command.fullName()
        );


        return profileRepository.save(profile);
    }
}
