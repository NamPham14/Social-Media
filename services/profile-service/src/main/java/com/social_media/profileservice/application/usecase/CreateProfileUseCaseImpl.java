package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.application.command.CreateProfileCommand;
import com.social_media.profileservice.domain.ProfileRepository;
import com.social_media.profileservice.domain.UserProfile;
import com.social_media.profileservice.domain.exception.DuplicateProfileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateProfileUseCaseImpl implements CreateProfileUseCase {

    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public UserProfile execute(CreateProfileCommand command) {

        if (profileRepository.findById(command.getId()).isPresent()) {
            throw new DuplicateProfileException("Profile with id " + command.getId() + " already exists");
        }


        UserProfile profile = UserProfile.createNewProfile(
                command.getId(),
                command.getUsername(),
                command.getFullName()
        );


        return profileRepository.save(profile);
    }
}
