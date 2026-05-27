package com.social_media.profileservice.domain;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {

    UserProfile save(UserProfile profile);
    Optional<UserProfile> findById(UUID id);
}
