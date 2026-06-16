package com.social_media.profileservice.application.usecase;

import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import org.springframework.data.domain.Page;

public interface SearchProfilesUseCase {
    Page<Profile> execute(String keyword, int page, int size);
}
