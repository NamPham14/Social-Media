package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.application.command.UpdateProfileCommand;
import com.social_media.profileservice.domain.UserProfile;
import com.social_media.profileservice.domain.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCaseImpl implements UpdateProfileUseCase {
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public UserProfile execute(UpdateProfileCommand command) {
        UserProfile profile = profileRepository.findById(command.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.updateInfo(command.getFullName(), command.getBio(), command.getAvatarUrl());
        return profileRepository.save(profile);
    }
}
