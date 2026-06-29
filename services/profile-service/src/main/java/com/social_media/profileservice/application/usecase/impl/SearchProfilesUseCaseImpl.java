package com.social_media.profileservice.application.usecase.impl;

import com.social_media.profileservice.application.usecase.SearchProfilesUseCase;
import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import com.social_media.profileservice.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchProfilesUseCaseImpl implements SearchProfilesUseCase {
    private final ProfileRepository profileRepository;
    @Override
    public Page<Profile> execute(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return profileRepository.searchProfiles(keyword, pageable);
    }
}
