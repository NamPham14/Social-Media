package com.social_media.profileservice.domain.repository;

import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {
    Profile save(Profile profile);
    Optional<Profile> findById(UUID id);
}
