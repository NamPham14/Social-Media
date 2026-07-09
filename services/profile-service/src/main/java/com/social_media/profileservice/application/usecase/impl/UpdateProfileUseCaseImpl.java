package com.social_media.profileservice.application.usecase.impl;

import com.social_media.profileservice.application.command.UpdateProfileCommand;
import com.social_media.profileservice.application.usecase.UpdateProfileUseCase;
import com.social_media.profileservice.application.exception.ProfileNotFoundException;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCaseImpl implements UpdateProfileUseCase {
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public Profile execute(UpdateProfileCommand command) {
    //Tìm Profile (Aggregate) qua Repository Adapter
        Profile profile = profileRepository.findById(command.id())
                .orElseThrow(ProfileNotFoundException::new);
    // Goi logic nghiep vu ngay trong Domain Aggregate
        profile.updateInfo(command.fullName(),command.bio(), command.avatarUrl());
// Lưu lại thông qua Repository Adapter (Adapter sẽ tự map sang Entity để lưu DB)
        return profileRepository.save(profile);
    }
}
