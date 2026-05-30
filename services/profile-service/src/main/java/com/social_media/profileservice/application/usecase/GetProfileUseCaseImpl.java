package com.social_media.profileservice.application.usecase;


import com.social_media.profileservice.domain.ProfileRepository;
import com.social_media.profileservice.domain.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetProfileUseCaseImpl implements GetProfileUseCase {
    private final ProfileRepository profileRepository;
    @Override
    public UserProfile execute(UUID id) {

        return profileRepository.findById(id).orElseThrow(() -> new RuntimeException("Profile not found"));
    }
}
