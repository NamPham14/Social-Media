package com.social_media.profileservice.domain.repository;

import com.social_media.profileservice.domain.model.profile.aggregate.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {
    Profile save(Profile profile);
    Optional<Profile> findById(UUID id);
    Page<Profile> searchProfiles(String keyword, Pageable pageable);
}
