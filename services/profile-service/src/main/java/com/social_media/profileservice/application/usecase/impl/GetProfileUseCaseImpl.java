package com.social_media.profileservice.application.usecase.impl;


import com.social_media.profileservice.application.usecase.GetProfileUseCase;
import com.social_media.profileservice.application.exception.ProfileNotFoundException;
import com.social_media.profileservice.domain.model.aggregate.Profile;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProfileUseCaseImpl implements GetProfileUseCase {
    private final ProfileRepository profileRepository;
    @Override
    public Profile execute(UUID id) {

        return profileRepository.findById(id)
                .orElseThrow(ProfileNotFoundException::new);
    }
}
